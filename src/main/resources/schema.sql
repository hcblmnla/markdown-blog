create table author
(
    id    bigint auto_increment primary key,
    login varchar(100) not null unique,
    name  varchar(100) not null
);

create table topic
(
    id        bigint auto_increment primary key,
    author_id bigint       not null,
    title     varchar(100) not null unique,
    foreign key (author_id) references author (id) on delete cascade
);

create table post
(
    id         bigint auto_increment primary key,
    topic_id   bigint       not null,
    title      varchar(100) not null unique,
    content    clob         not null,
    updated_at timestamp default current_timestamp,
    foreign key (topic_id) references topic (id) on delete cascade
);

create table tag
(
    id   bigint auto_increment primary key,
    name varchar(100) not null unique
);

create table post_tags
(
    post_id bigint not null,
    tag_id  bigint not null,
    primary key (post_id, tag_id),
    foreign key (post_id) references post (id) on delete cascade,
    foreign key (tag_id) references tag (id) on delete cascade
);
