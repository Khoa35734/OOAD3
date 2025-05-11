create database ooad;
use ooad;

create table users(
	id int auto_increment,
    name nvarchar(50),
    
    primary key (id)
);
alter table users add phone_number char(10);
create table take(
	user_id int,
    appointment_id int,
    primary key(user_id, appointment_id)
);

create table appointment(
	id int auto_increment,
    name nvarchar(50),
    location nvarchar(50),
    meeting_date datetime,
    start_hour int,
    end_hour int,
    type_appointment varchar(20) ,
    primary key(id)
);

create table take_rmd(
	appointment_id int,
    reminder_id int,
    primary key(appointment_id, reminder_id)
);

create table reminder(
	id int auto_increment,
	title nvarchar(50),
    primary key(id)
);
alter table take add constraint fk_take_users_id foreign key (user_id) references users(id);
alter table take add constraint fk_take_appointment_id foreign key (appointment_id) references appointment(id);
alter table take_rmd add constraint fk_takermd_reminder_id foreign key (reminder_id) references reminder(id);
alter table take_rmd add constraint fk_takermd_appointment_id foreign key (appointment_id) references appointment(id);


insert into users(name, phone_number) values ('Huỳnh Trương Thảo Duyên', '0327420207');
insert into users(name, phone_number) values ('Lê Thị Chính', '7773413122');
insert into users(name, phone_number) values ('Phạm Khoa', '0321484321');
insert into take(user_id, appointment_id) values ('1', '1');
insert into appointment(name, location, meeting_date, start_hour, end_hour, type_appointment) values ('Concert', 'A101', '2024-05-11', 7, 10, 'Đơn');
insert into appointment(name, location, meeting_date, start_hour, end_hour, type_appointment) values ('GroupTest', 'E209', '2024-05-12', 18, 19, 'Nhóm');
insert into reminder(title) values ('15 phút');
insert into reminder(title) values ('30 phút');
insert into reminder(title) values ('45 phút');
insert into take_rmd(appointment_id, reminder_id) values (2, 1);
insert into take_rmd(appointment_id, reminder_id) values (2, 2);
select * from appointment where name = 'GroupTest' and type_appointment = ? and meeting_date = ? and start_hour = ? and end_hour = ?;
select * from reminder;
select * from take_rmd;
select * from take;
select * from appointment join take_rmd on id = appointment_id where id = 1

