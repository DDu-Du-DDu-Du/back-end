FROM mysql:latest

ADD schema.sql /docker-entrypoint-initdb.d/

ENV MYSQL_ROOT_PASSWORD=ddudu-local-root
ENV MYSQL_USER=ddudu

EXPOSE 3306
