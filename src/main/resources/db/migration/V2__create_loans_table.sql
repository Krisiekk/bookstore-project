CREATE TABLE loans (
                       id BIGSERIAL PRIMARY KEY,
                       reservation_id BIGINT NOT NULL UNIQUE,
                       user_id BIGINT NOT NULL,
                       book_id BIGINT NOT NULL,
                       borrowed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       returned_at TIMESTAMP,
                       status VARCHAR(30) NOT NULL,

                       CONSTRAINT fk_loan_reservation
                           FOREIGN KEY (reservation_id)
                               REFERENCES reservations(id),

                       CONSTRAINT fk_loan_user
                           FOREIGN KEY (user_id)
                               REFERENCES users(id),

                       CONSTRAINT fk_loan_book
                           FOREIGN KEY (book_id)
                               REFERENCES books(id)
);
