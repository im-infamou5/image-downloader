FROM liquibase/liquibase AS migration

ENV LIQUIBASE_LOG_LEVEL=debug

CMD ["liquibase", "--url=jdbc:postgresql://postgres:5432/images?user=user&password=12345", "--changeLogFile=changelog.yaml", "update"]
