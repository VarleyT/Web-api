create table if not exists free_game
(
    id         int auto_increment
        primary key,
    game_name  varchar(255)  not null comment '游戏名',
    url        varchar(1023) not null comment '链接',
    type       varchar(127)  null comment '类型',
    start_time datetime      not null comment '开始时间',
    end_time   datetime      not null comment '结束时间',
    valid      tinyint       null comment '是否永久',
    store      varchar(15)   null comment '游戏平台',
    constraint free_game_id_uindex
        unique (id),
    constraint game_name
        unique (game_name)
)
    comment '游戏白嫖信息';


