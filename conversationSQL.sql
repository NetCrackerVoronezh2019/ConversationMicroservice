INSERT into Users values (1, 'Migor');
INSERT into Users values (2, 'Armen');
INSERT into Dialogs values (1, now(), 1, 'testchat1');
INSERT into Dialogs values (2, now(), 1, 'testchat3');
insert into user_dialog values (1,1);
insert into user_dialog values (1,2);
insert into user_dialog values (2,1);
insert into user_dialog values (2,2);
insert into messages values (1,now(),false,'test Message from Migor',1,1);
insert into messages values (1,now(),false,'test Message from Armen',1,2);