package ru.DTO;

import org.springframework.beans.factory.annotation.Autowired;
import ru.domen.Dialog;
import ru.domen.Message;
import ru.domen.User;
import ru.services.MessageService;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


public class DialogDTO {
    private Integer dialogId;
    private String name;
    private LocalDateTime creationDate;
    private Integer creatorId;
    private List<UserDTO> users;
    private List<MessageDTO> messages;
    private Long countNotification;
    private LocalDateTime lastMessageDate;
    private String type;
    private String image;

    public Long getCountNotification() {
        return countNotification;
    }

    public void setCountNotification(Long countNotification) {
        this.countNotification = countNotification;
    }

    public LocalDateTime getLastMessageDate() {
        return lastMessageDate;
    }

    public DialogDTO(Dialog dialog) {
        this.dialogId = dialog.getDialogId();
        this.name = dialog.getName();
        creationDate = dialog.getCreationDate();
        creatorId = dialog.getCreatorId();
        type = dialog.getType().getTypeName();
        this.image = dialog.getImage();
    }

    public static DialogDTO getDialogDTO(Dialog dialog) {
        DialogDTO dialogDTO = new DialogDTO(dialog);
        dialogDTO.users = new ArrayList<>();
        dialogDTO.messages = new ArrayList<>();
        if (!dialog.getMessages().isEmpty()) {
            dialogDTO.lastMessageDate = dialog.getMessages().stream().sorted(Comparator.comparing(Message::getDate).reversed()).collect(Collectors.toList()).get(0).getDate();
        } else  {
            dialogDTO.lastMessageDate = dialogDTO.creationDate;
        }
        for (User user:
                dialog.getUsers()) {
            dialogDTO.users.add(new UserDTO(user));
        }
        for (Message message:
                dialog.getMessages()) {
            dialogDTO.messages.add(new MessageDTO(message));
        }
        return dialogDTO;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public DialogDTO() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getDialogId() {
        return dialogId;
    }

    public void setDialogId(Integer dialogId) {
        this.dialogId = dialogId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public void setLastMessageDate(LocalDateTime lastMessageDate) {
        this.lastMessageDate = lastMessageDate;
    }

    public Integer getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Integer creatorId) {
        this.creatorId = creatorId;
    }

    public List<UserDTO> getUsers() {
        return users;
    }

    public void setUsers(List<UserDTO> users) {
        this.users = users;
    }

    public List<MessageDTO> getMessages() {
        return messages;
    }

    public void setMessages(List<MessageDTO> messages) {
        this.messages = messages;
    }
}
