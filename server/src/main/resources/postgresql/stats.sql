create table stats (
    player_id varchar(36) not null,
    stat_name varchar(32) not null,
    create_date timestamptz not null default now(),
    update_date timestamptz not null default now(),
    stat int not null default 0,
    primary key (player_id, stat_name)
);

create index index_player_id on stats (player_id);

insert into stats (player_id, stat_name) values ('global', 'blocksBroken');
