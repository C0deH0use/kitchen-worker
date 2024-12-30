.PHONY: build run run-dependencies run-db destroy

build:
	docker compose build worker

run: build
	docker compose up worker

run-dependencies:
	docker compose --profile dependencies up --force-recreate --renew-anon-volumes

destroy:
	docker compose down -v
	docker container prune --force
