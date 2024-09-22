CREATE USER acmeuser WITH PASSWORD 'password' SUPERUSER;
CREATE DATABASE acme owner acmeuser encoding 'UTF8';
GRANT ALL PRIVILEGES ON DATABASE acme TO acmeuser;

\c acme;

create schema acme;

--
-- PostgreSQL database dump
--

SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

ALTER SYSTEM SET max_connections = 5000;
ALTER SYSTEM RESET shared_buffers;
