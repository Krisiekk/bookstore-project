package pl.kpietrzak.bookstore.exception;

public class BookAlreadyLoanedException extends RuntimeException {

    public BookAlreadyLoanedException(Long bookId) {
        super("Book is already loaned with id: " + bookId);
    }
}
