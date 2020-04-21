package ru.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import ru.DTO.AmazonModel;
import ru.DTO.DialogDTO;
import ru.DTO.MessageDTO;
import ru.DTO.UserDTO;
import ru.domen.Dialog;
import ru.domen.Message;
import ru.domen.MessageFile;
import ru.domen.User;
import ru.services.*;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "http://localhost:9080")
public class DialogController {

    @Autowired
    private DialogService dialogService;
    @Autowired
    private MessageService messageService;
    @Autowired
    private UserService userService;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private DialogTypeService dialogTypeService;
    @Autowired
    private MessageFileService messageFileService;

    @PostMapping("/dialogCreate/")
    public void createDialog(@RequestBody DialogDTO dialogDTO) {
        Dialog dialog = new Dialog();
        dialog.setName(dialogDTO.getName());
        dialog.setCreatorId(dialogDTO.getCreatorId());
        dialog.setType(dialogTypeService.getDialogTypeByName(dialogDTO.getType()));
        dialog.setCreationDate(new Date());
        dialog.setType(dialogTypeService.getDialogTypeByName("public"));
        dialog = dialogService.saveDialog(dialog);
        String key = "dialog_"+ dialog.getDialogId() + "_avatar";
        AmazonModel amazonModel = new AmazonModel(key,dialogDTO.getImage());
        dialog.setImage(key);
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<AmazonModel> amazonModelHttpEntity = new HttpEntity<>(amazonModel);
        restTemplate.exchange("http://localhost:1234/dialog/uploadFile", HttpMethod.POST,amazonModelHttpEntity,Object.class);
        dialogService.saveDialog(dialog);
    }

    @PutMapping("dialog/settings")
    public void dialogSettings(@RequestBody DialogDTO dialogDTO) {
        Dialog dialog = dialogService.getDialogById(dialogDTO.getDialogId());
        dialog.setName(dialogDTO.getName());
        dialogService.saveDialog(dialog);
    }

    @PutMapping("dialog/setAvatar")
    public void dialogSetAvarar(@RequestBody DialogDTO dialogDTO) {
        Dialog dialog = dialogService.getDialogById(dialogDTO.getDialogId());
        AmazonModel amazonModel = new AmazonModel(dialog.getImage(),dialogDTO.getImage());
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<AmazonModel> amazonModelHttpEntity = new HttpEntity<>(amazonModel);
        restTemplate.exchange("http://localhost:1234/dialog/uploadFile", HttpMethod.POST,amazonModelHttpEntity,Object.class);
    }

    @PutMapping("dialog/setMessage")
    public void setMessage(@RequestBody MessageDTO messageDTO) {
        Message message = messageService.getById(messageDTO.getMessageId());
        message.setText(messageDTO.getText());
        message.setModified(true);
        messageService.addMessage(message);
    }

    @GetMapping("/getDialogMembers/")
    public List<UserDTO> getDialogs(@RequestParam(name = "dialogId") Integer dialogId,@RequestParam Integer userId) {
        final List<UserDTO> users = dialogService.getDialogDTOById(dialogId).getUsers();
        return users;
    }

    @GetMapping("/getDialogMessages/")
    public List<MessageDTO> getMessages(@RequestParam(name = "dialogId") Integer dialogId, @RequestParam Integer userId) {
        return messageService.getDialogMessages(dialogId).stream().sorted(Comparator.comparing(MessageDTO::getDate).reversed()).collect(Collectors.toList());
    }

    @GetMapping("/getDialog/")
    public DialogDTO getDialogInfo(@RequestParam(name = "dialogId") Integer dialogId,@RequestParam Integer userId) {
        DialogDTO dialog = dialogService.getDialogDTOById(dialogId);
        User user = userService.getUserById(userId);
        dialog.setCountNotification(user.getNotifications().stream().filter(notification -> notification.getMessage().getDialog().getDialogId() == dialogId).count());
        return dialog;
    }

    @DeleteMapping("/liveDialog/")
    public void liveDialog(@RequestParam(name = "userId") Integer userId,@RequestParam(name = "dialogId") Integer dialogId) {
        Dialog dialog = dialogService.getDialogById(dialogId);
        dialogService.deleteUserFromDialog(userId,dialog);
        if (dialog.getUsers().size() == 0) {
            dialogService.deleteDialogById(dialogId);
        }
    }

    @DeleteMapping("/deleteDialog/")
    public void deleteDialog(@RequestParam(name = "dialogId") Integer dialogId) {
        messageService.deleteMessageByDialogId(dialogId);
        dialogService.deleteDialogById(dialogId);
    }

    @GetMapping("/addUserInDialog/")
    public void addUserInDialog(@RequestParam(name = "userId" ) Integer userId, @RequestParam(name = "dialogId") Integer dialogId, @RequestParam Integer adderId) {
        User user = userService.getUserById(userId);
        if (user != null) {
            Dialog dialog = dialogService.getDialogById(dialogId);
            if (!dialog.getUsers().contains(user)) {
                dialogService.addUserInDialog(dialog, user);
            }
        }
    }


    @PostMapping("/sendMessage/")
    public MessageDTO sendMessage(@RequestBody MessageDTO messageDTO) {
        Message message = new Message();
        message.setDialog(dialogService.getDialogById(messageDTO.getDialog()));
        message.setSender(userService.getUserById(messageDTO.getSender().getUserId()));
        message.setText(messageDTO.getText());
        message.setModified(messageDTO.isModified());
        message.setDate(new Date());
        message = messageService.addMessage(message);
        int i = 0;
        for (String file :
                messageDTO.getFiles()) {
            String key = "Message_" + message.getMessageId() + "+file_" + i;
            AmazonModel amazonModel = new AmazonModel(key,file);
            MessageFile messageFile = new MessageFile();
            messageFile.setFile(key);
            messageFile.setMessage(message);
            messageFileService.saveMessageFile(messageFile);
            RestTemplate restTemplate = new RestTemplate();
            HttpEntity<AmazonModel> amazonModelHttpEntity = new HttpEntity<>(amazonModel);
            restTemplate.exchange("http://localhost:1234/dialog/uploadFile", HttpMethod.POST,amazonModelHttpEntity,Object.class);
            i++;
        }
        message = messageService.getById(message.getMessageId());
        notificationService.addNotification(message);
        return new MessageDTO(message);
    }
}