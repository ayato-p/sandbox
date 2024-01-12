#!/bin/bash

psql -U postgres --dbname "$POSTGRES_DB" < /data/sql/00_init.sql
psql -U acmeuser --dbname acme < /data/sql/01_ddl.sql
