-- auto-generated definition
create table user
(
    username     varchar(256)                       null comment '用户名',
    id           bigint auto_increment
        primary key,
    userAccount  varchar(256)                       null comment '用户账号',
    avatarUrl    varchar(1024)                      null comment '头像',
    gender       tinyint                            null comment '性别',
    userPassword varchar(512)                       not null comment '密码',
    phone        varchar(128)                       null comment '电话',
    email        varchar(512)                       null comment '邮箱',
    userStatus   int      default 0                 null comment '状态',
    createTime   datetime default CURRENT_TIMESTAMP null comment '创建时间',
    updateTime   datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '创建时间',
    isDelete     tinyint  default 0                 null,
    userRole     int      default 0                 not null comment '用户角色 0普通用户1管理员',
    tags         varchar(1024)                      null comment '标签 json 列表'
)
    comment '用户';

-- auto-generated definition
create table team
(
    id          bigint auto_increment comment 'id'
        primary key,
    name        varchar(256)                               not null comment '标队伍名',
    description varchar(1024)                              null comment '描述',
    maxNum      int              default 1                 not null comment '最大人数',
    expireTime  datetime                                   null comment '过期时间',
    userId      bigint                                     null comment '用户id',
    status      int              default 0                 not null comment '0 - 公开，1 - 私有，2 - 加密',
    password    varchar(512)                               null comment '密码',
    creatTime   datetime         default CURRENT_TIMESTAMP null comment '创建时间',
    updateTime  datetime         default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete    tinyint unsigned default '0'               not null comment '是否创建'
)
    comment '队伍';

-- auto-generated definition
create table team_notification
(
    id            int auto_increment
        primary key,
    acceptUserId  int                                not null comment '接受消息的用户id',
    operateUserId int                                not null comment '进行操作的用户id',
    teamId        int                                not null comment '队伍Id',
    status        tinyint                            not null comment '0 成员退出队伍
1 队长退出队伍
2 队长解散队伍
',
    createTime    datetime default CURRENT_TIMESTAMP null comment '创建时间',
    updateTime    datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '创建时间',
    isDelete      tinyint  default 0                 null,
    constraint team_notification_id_uindex
        unique (id)
);

-- auto-generated definition
create table user_team
(
    id         bigint auto_increment comment 'id'
        primary key,
    userId     bigint                                     null comment '用户id',
    teamId     bigint                                     null comment '队伍id',
    joinTime   datetime                                   null comment '加入时间',
    creatTime  datetime         default CURRENT_TIMESTAMP null comment '创建时间',
    updateTime datetime         default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete   tinyint unsigned default '0'               not null comment '是否创建'
)
    comment '用户队伍关系';

-- auto-generated definition
create table friend
(
    id          bigint auto_increment
        primary key,
    userId      bigint                             not null comment '用户id',
    friendId    bigint                             not null comment '好友id',
    friendAlias varchar(256)                       null comment '用户对好友的备注',
    userAlias   varchar(256)                       null comment '好友对用户的备注',
    reason      varchar(1024)                      null comment '申请原因',
    status      tinyint  default 0                 not null comment '申请状态,0 申请中，1 已同意，2 已拒绝',
    createTime  datetime default CURRENT_TIMESTAMP null comment '创建时间',
    updateTime  datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete    tinyint  default 0                 not null
)
    comment '好友';

