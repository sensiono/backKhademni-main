package tn.esprit.pi.books;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.pi.books.*;
import tn.esprit.pi.common.PageResponse;
import tn.esprit.pi.entities.User;
import tn.esprit.pi.file.FileStorageService;
import tn.esprit.pi.history.BookTransactionHistory;
import tn.esprit.pi.history.BookTransactionHistoryRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import static tn.esprit.pi.books.BookSpecification.withOwnerId;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BookService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final BookTransactionHistoryRepository transactionHistoryRepository;
    private final FileStorageService fileStorageService;

    public Integer save(BookRequest request, Authentication connectedUser) {
        User user = ((User) connectedUser.getPrincipal());

        Book book = bookMapper.toBook(request);
        book.setOwner(user);

        return bookRepository.save(book).getId();
    }
    public List<Book> recommendBooksByTopGenre(Integer ownerId) {
        // Step 1: Retrieve user's top genre
        Genre topGenre = findTopGenre(findUserGenres(ownerId));

        // Step 2: Find books of top genre (if needed)

        // Step 3: Optionally, sort recommendations (if needed)
        // You can implement sorting logic here if required

        // Step 4: Return recommended books
        return findBooksByGenre(topGenre);
    }

    // Recommendation function to return books based on user's top genre
//    public List<Book> recommendBooksByTopGenre(Integer ownerId) {
//
//        // Step 1: Retrieve user's top genre
//        Genre topGenre = findUserTopGenre(ownerId);
//
//        // Step 2: Find books of top genre
//
//
//        // Step 3: Optionally, sort recommendations
//        // You can implement sorting logic here if needed
//
//        // Step 4: Return recommended books
//        return findBooksByGenre(topGenre);
//    }
    private Genre findTopGenre(List<Genre> genres) {
        // Check if the list of genres is empty
        if (genres.isEmpty()) {
            return null;
        }

        // Create a map to count occurrences of each genre
        Map<Genre, Long> genreCount = genres.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        // Sort the genres based on their occurrence count in descending order
        List<Genre> sortedGenres = genreCount.entrySet().stream()
                .sorted(Map.Entry.<Genre, Long>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        // Check if the top genre has significantly more occurrences than others
        // Here, you can define your own threshold for what constitutes "significantly more"
        long topGenreCount = genreCount.get(sortedGenres.get(0));
        if (sortedGenres.size() > 1) {
            long secondGenreCount = genreCount.get(sortedGenres.get(1));
            // You can adjust the comparison logic based on your requirements
            if (topGenreCount > 2 * secondGenreCount) {
                // If the top genre occurs more than twice as often as the second genre,
                // consider it as the dominant genre
                return sortedGenres.get(0);
            }
        }

        // If there's no significant difference in occurrence counts,
        // or if there's only one genre in the list, return the top genre
        return sortedGenres.get(0);
    }

    //test top genre
//    private Genre findTopGenre(List<Genre> genres) {
//        // Check if the list of genres is empty
//        if (genres.isEmpty()) {
//            return null;
//        }
//
//        // Create a map to count occurrences of each genre
//        Map<Genre, Long> genreCount = genres.stream()
//                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
//
//        // Sort the genres based on their occurrence count in descending order
//        List<Genre> sortedGenres = genreCount.entrySet().stream()
//                .sorted(Map.Entry.<Genre, Long>comparingByValue().reversed())
//                .map(Map.Entry::getKey)
//                .collect(Collectors.toList());
//
//        // Return the top genre (the first one in the sorted list)
//        return sortedGenres.get(0);
//    }

    // Method to find user's top genre jwha behy
//    public Genre findUserTopGenre(Integer ownerId) {
//        // Implement logic to determine user's top genre based on added books
//        List<Genre> userGenres = findUserGenres(ownerId);
//        return findTopGenre(userGenres);
//    }
    public Genre findUserTopGenre(Integer ownerId) {
        // Retrieve genres of books added by the user
        List<Genre> userGenres = findUserGenres(ownerId);

        // Count occurrences of each genre
        Map<Genre, Long> genreCounts = userGenres.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        // Find the genre with the highest count

        return genreCounts.entrySet().stream()
                .max(Comparator.comparing(Map.Entry::getValue))
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    // Method to find genres of books added by the user
    public List<Genre> findUserGenres(Integer ownerId) {
        // Retrieve books added by the user with the given ownerId
        List<Book> userBooks = bookRepository.findByOwnerId(ownerId);

        // Extract genres from the user's books
        return userBooks.stream()
                .map(Book::getGenre)
                .distinct()
                .collect(Collectors.toList());
    }

    // Method to find books by genre
    public List<Book> findBooksByGenre(Genre genre) {
        // Implement logic to retrieve recommended books based on genre
        return bookRepository.findByGenre(genre);
    }

    // Method to find top genre from a list of genres
//    private Genre findTopGenre(List<Genre> genres) {
//        // Check if the list of genres is empty
//        if (genres.isEmpty()) {
//            return null;
//        }
//
//        // Create a map to count occurrences of each genre
//        Map<Genre, Long> genreCount = genres.stream()
//                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
//
//        // Find the genre with the highest count
//        Optional<Map.Entry<Genre, Long>> topEntry = genreCount.entrySet().stream()
//                .max(Map.Entry.comparingByValue());
//
//        // Return the top genre if found, otherwise return null
//        return topEntry.isPresent() ? topEntry.get().getKey() : null;
//    }

    // Method to find genres of books added by the user
//    public List<Genre> findUserGenres(Integer ownerId) {
//        // Retrieve books added by the user with the given ownerId
//        List<Book> userBooks = bookRepository.findByOwnerId(ownerId);
//
//        // Extract genres from the user's books
//        return userBooks.stream()
//                .map(Book::getGenre)
//                .distinct()
//                .collect(Collectors.toList());
//    }


    public List<Book> findBooksByGenres(List<Genre> userGenres) {
        // Implement logic to retrieve recommended books based on user's genres
        return bookRepository.findByGenreIn(userGenres);
    }
    public BookResponse findById(Integer bookId) {
        return bookRepository.findById(bookId)
                .map(bookMapper::toBookResponse)
                .orElseThrow(() -> new EntityNotFoundException("No book found with ID:: " + bookId));
    }

    public PageResponse<BookResponse> findAllBooks(int page, int size, Authentication connectedUser) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new RuntimeException("User not authenticated");
        }
        User user = ((User) connectedUser.getPrincipal());
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<Book> books = bookRepository.findAllDisplayableBooks(pageable, user.getId());
        List<BookResponse> booksResponse = books.stream()
                .map(bookMapper::toBookResponse)
                .toList();
        return new PageResponse<>(
                booksResponse,
                books.getNumber(),
                books.getSize(),
                books.getTotalElements(),
                books.getTotalPages(),
                books.isFirst(),
                books.isLast()
        );
    }

    public List<Book> getAll() {
        return (List<Book>) bookRepository.findAll();
    }

    public PageResponse<BookResponse> findAllBooksByOwner(int page, int size, Authentication connectedUser) {
        User user = ((User) connectedUser.getPrincipal());
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<Book> books = bookRepository.findAll(withOwnerId(user.getId()), pageable);
        List<BookResponse> booksResponse = books.stream()
                .map(bookMapper::toBookResponse)
                .toList();
        return new PageResponse<>(
                booksResponse,
                books.getNumber(),
                books.getSize(),
                books.getTotalElements(),
                books.getTotalPages(),
                books.isFirst(),
                books.isLast()
        );
    }
    @Transactional
    public Book modifyBook(Book book, User currentUser) {
        // Retrieve the book to be modified
        Book existingBook = bookRepository.findById(book.getId())
                .orElseThrow(() -> new EntityNotFoundException("No book found with ID: " + book.getId()));

        // Check if the current user is authorized to update the book
        if (!existingBook.getOwner().equals(currentUser)) {
            throw new SecurityException("You are not authorized to modify this book");
        }

        // Update the book data with the new values
        existingBook.setTitle(book.getTitle());
        existingBook.setAuthorName(book.getAuthorName());
        // Update other attributes as needed

        // Save the updated book
        return bookRepository.save(existingBook);
    }





    public Integer updateShareableStatus(Integer bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("No book found with ID:: " + bookId));
        User user = ((User) connectedUser.getPrincipal());
        if (!Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("You cannot update others books shareable status");
        }
        book.setShareable(!book.isShareable());
        bookRepository.save(book);
        return bookId;
    }

    public Integer updateArchivedStatus(Integer bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("No book found with ID:: " + bookId));
        User user = ((User) connectedUser.getPrincipal());
        if (!Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("You cannot update others books archived status");
        }
        book.setArchived(!book.isArchived());
        bookRepository.save(book);
        return bookId;
    }


    public Integer borrowBook(Integer bookId,Integer userID) {
        // Retrieve the currently logged-in user

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("No book found with ID:: " + bookId));
        if (Objects.equals(book.getOwner().getId(), userID)) {
            throw new OperationNotPermittedException("You cannot borrow your own book");
        }

        final boolean isAlreadyBorrowedByUser = transactionHistoryRepository.isAlreadyBorrowedByUser(bookId,userID);
        if (isAlreadyBorrowedByUser) {
            throw new OperationNotPermittedException("You already borrowed this book and it is still not returned or the return is not approved by the owner");
        }

        final boolean isAlreadyBorrowedByOtherUser = transactionHistoryRepository.isAlreadyBorrowed(bookId);
        if (isAlreadyBorrowedByOtherUser) {
            throw new OperationNotPermittedException("The requested book is already borrowed");
        }

        BookTransactionHistory bookTransactionHistory = BookTransactionHistory.builder()
//                .userID()
                .book(book)
                .returned(false)
                .returnApproved(false)
                .build();
        return transactionHistoryRepository.save(bookTransactionHistory).getId();
    }

    public Integer returnBorrowedBook(Integer bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("No book found with ID:: " + bookId));
        if (book.isArchived() || !book.isShareable()) {
            throw new OperationNotPermittedException("The requested book is archived or not shareable");
        }
        User user = ((User) connectedUser.getPrincipal());
        if (Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("You cannot borrow or return your own book");
        }

        BookTransactionHistory bookTransactionHistory = transactionHistoryRepository.findByBookIdAndUserId(bookId, user.getId())
                .orElseThrow(() -> new OperationNotPermittedException("You did not borrow this book"));

        bookTransactionHistory.setReturned(true);
        return transactionHistoryRepository.save(bookTransactionHistory).getId();
    }

    public Integer approveReturnBorrowedBook(Integer bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("No book found with ID:: " + bookId));
        if (book.isArchived() || !book.isShareable()) {
            throw new OperationNotPermittedException("The requested book is archived or not shareable");
        }
        User user = ((User) connectedUser.getPrincipal());
        if (!Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("You cannot approve the return of a book you do not own");
        }

        BookTransactionHistory bookTransactionHistory = transactionHistoryRepository.findByBookIdAndOwnerId(bookId, user.getId())
                .orElseThrow(() -> new OperationNotPermittedException("The book is not returned yet. You cannot approve its return"));

        bookTransactionHistory.setReturnApproved(true);
        return transactionHistoryRepository.save(bookTransactionHistory).getId();
    }
//
//    public void uploadBookCoverPicture(MultipartFile file, Authentication connectedUser, Integer bookId) {
//        Book book = bookRepository.findById(bookId)
//                .orElseThrow(() -> new EntityNotFoundException("No book found with ID:: " + bookId));
//        User user = ((User) connectedUser.getPrincipal());
//        var profilePicture = fileStorageService.saveFile(file, bookId, Long.valueOf(user.getId()));
//        book.setBookCover(profilePicture);
//        bookRepository.save(book);
//    }

    public PageResponse<BorrowedBookResponse> findAllBorrowedBooks(int page, int size, Authentication connectedUser) {
        User user = ((User) connectedUser.getPrincipal());
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<BookTransactionHistory> allBorrowedBooks = transactionHistoryRepository.findAllBorrowedBooks(pageable, user.getId());
        List<BorrowedBookResponse> booksResponse = allBorrowedBooks.stream()
                .map(bookMapper::toBorrowedBookResponse)
                .toList();
        return new PageResponse<>(
                booksResponse,
                allBorrowedBooks.getNumber(),
                allBorrowedBooks.getSize(),
                allBorrowedBooks.getTotalElements(),
                allBorrowedBooks.getTotalPages(),
                allBorrowedBooks.isFirst(),
                allBorrowedBooks.isLast()
        );
    }

    public PageResponse<BorrowedBookResponse> findAllReturnedBooks(int page, int size, Authentication connectedUser) {
        User user = ((User) connectedUser.getPrincipal());
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<BookTransactionHistory> allBorrowedBooks = transactionHistoryRepository.findAllReturnedBooks(pageable, user.getId());
        List<BorrowedBookResponse> booksResponse = allBorrowedBooks.stream()
                .map(bookMapper::toBorrowedBookResponse)
                .toList();
        return new PageResponse<>(
                booksResponse,
                allBorrowedBooks.getNumber(),
                allBorrowedBooks.getSize(),
                allBorrowedBooks.getTotalElements(),
                allBorrowedBooks.getTotalPages(),
                allBorrowedBooks.isFirst(),
                allBorrowedBooks.isLast()
        );
    }
}