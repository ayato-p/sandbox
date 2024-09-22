\c acme;

create type acme.mood as enum ('sad', 'ok', 'happy');

create table acme.person (
    name text, 
    current_mood acme.mood
);