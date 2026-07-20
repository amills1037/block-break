create table stats (
    player_id varchar(36) not null,
    stat_name varchar(32) not null,
    create_date datetime not null default now(),
    update_date datetime not null default now(),
    stat int not null default 0,
    index player_id (player_id),
    primary key (player_id, stat_name)
);

insert into stats (player_id, stat_name) values ('global', 'blocksBroken');
