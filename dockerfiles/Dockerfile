FROM postgres:12

ENV POSTGRES_USER="postgres" \
    POSTGRES_PASSWORD="postgres" \
    POSTGRES_INITDB_ARGS="--encoding=UTF-8 --locale=C"

RUN apt-get update -y
RUN apt-get install -y openjdk-11-jdk

COPY --chown=postgres:postgres ./sql/setup.sql /docker-entrypoint-initdb.d/
COPY ./jar/postgresql-42.2.22.jar /
CMD ["postgres"]