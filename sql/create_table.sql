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
create table tag
(
    id         bigint auto_increment comment 'id'
        primary key,
    tagName    varchar(256)                           null comment '标签名称',
    userID     bigint                                 null comment '用户 id',
    parentId   bigint                                 null comment '父标签 id',
    isParent   tinyint                                null comment '0 - 不是 1 - 是',
    creatTime  datetime default CURRENT_TIMESTAMP     null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP     null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete   datetime default '0000-00-00 00:00:00' not null comment '是否创建',
    constraint uniIdx__tagName
        unique (tagName)
)
    comment '标签';

create index idx__userId
    on tag (userID);