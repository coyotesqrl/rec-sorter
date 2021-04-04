# rec-sorter
Quickie sorting app.

### Quickstart
* `make test` to run all unit tests with coverage
* `make lint` to run various static analysis tools against code
* `make fmt` to re-format the code
* `make main` to run commandline file formatter
    - First prompts for filename
    - Then prompts for sorting option
        - 1: Email, descending; then last name, ascending, if there are email collisions
        - 2: Birth date, ascending
        - 3: Last name, descending
* `make web` starts simple webserver listening on port 4000 with four endpoints:
    - `POST /records` takes a single line of plain text, formatted like one of the three input files in [test](./test)
    - `GET /records/email` returns the records in the system sorted by email, ascending
    - `GET /records/birthdate` returns the records in the system sorted by birth date, ascending
    - `GET /records/name` returns the records in the system sorted by last name, ascending
    