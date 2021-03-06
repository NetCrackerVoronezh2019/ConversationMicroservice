package ru.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.domen.Dialog;
import ru.services.DialogService;
import ru.services.DialogTypeService;
import ru.services.MessageService;
import ru.services.UserService;

import java.time.LocalDateTime;
import java.util.Date;

@RestController
public class AdvertisementController {
    @Autowired
    private DialogService dialogService;
    @Autowired
    private DialogTypeService dialogTypeService;
    @Autowired
    private UserService userService;
    @Autowired
    private MessageService messageService;

    @PostMapping("/advertisement/createDialog")
    public Integer createGroupDialog(@RequestParam Integer creatorId,@RequestParam Integer userId, @RequestParam String advertisementName,@RequestParam(required = false) String key) {
        Dialog dialog =new Dialog();
        dialog.setCreationDate(LocalDateTime.now().plusHours(3));
        dialog.setCreatorId(creatorId);
        dialog.setName(advertisementName);
        dialog.getUsers().add(userService.getUserById(userId));
        dialog.getUsers().add(userService.getUserById(creatorId));
        dialog.setType(dialogTypeService.getDialogTypeByName("advertisement"));
        if (key != null) {
            dialog.setImage(key);
        }
        return dialogService.saveDialog(dialog).getDialogId();
    }

    @DeleteMapping("/advertisement/deleteDialog/")
    public void deleteDialog(@RequestParam(name = "dialogId") Integer dialogId) {
        messageService.deleteMessageByDialogId(dialogId);
        dialogService.deleteDialogById(dialogId);
    }
}
