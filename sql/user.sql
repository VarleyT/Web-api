create table if not exists user
(
    id          int auto_increment
        primary key,
    user_name   varchar(16) collate utf8mb4_bin not null comment '用户名',
    passwd      varchar(128)                    not null comment '密码',
    create_time varchar(128)                    not null comment '创建时间',
    status      int default 1                   not null comment '状态',
    constraint idx_username
        unique (user_name)
);


