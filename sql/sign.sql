create table if not exists sign
(
    id        int          not null
        primary key,
    user_name varchar(127) not null comment '用户名',
    frequency int          null comment '连续签到次数',
    sign_time datetime     null comment '签到时间',
    constraint sign_user_name_uindex
        unique (user_name)
)
    comment '用户签到';


