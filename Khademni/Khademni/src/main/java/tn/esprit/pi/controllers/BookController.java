package tn.esprit.pi.controllers;


import io.swagger.v3.oas.annotations.Parameter;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.pi.books.*;
import tn.esprit.pi.common.PageResponse;
import tn.esprit.pi.entities.User;

import java.util.List;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService service;
//    private final AuthenticationService authenticationService ;


    @PostMapping("/saveBook")
    public ResponseEntity<Integer> saveBook(
            @Valid @RequestBody BookRequest request,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(service.save(request, connectedUser));
    }

    @PostMapping("/borrow/{book-id}")
    public ResponseEntity<Integer> borrow(
            @PathVariable("book-id") Integer bookId,
            @RequestParam("userID") Integer userId
    ) {
        try {
            Integer transactionId = service.borrowBook(bookId, userId);
            return ResponseEntity.ok(transactionId);
        } catch (EntityNotFoundException | OperationNotPermittedException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }


    @GetMapping("findBookById/{book-id}")
    public ResponseEntity<BookResponse> findBookById(
            @PathVariable("book-id") Integer bookId
    ) {
        return ResponseEntity.ok(service.findById(bookId));
    }
    @GetMapping("/findUserTopGenre")
    public ResponseEntity<Genre> findUserTopGenre(
            @RequestParam("ownerId") Integer ownerId
    ) {
        Genre topGenre = service.findUserTopGenre(ownerId);
        return ResponseEntity.ok(topGenre);
    }
    @GetMapping("/recommendBooksByTopGenre")
    public ResponseEntity<List<Book>> recommendBooksByTopGenre(
            @RequestParam("ownerId") Integer ownerId
    ) {
        List<Book> recommendedBooks = service.recommendBooksByTopGenre(ownerId);
        return ResponseEntity.ok(recommendedBooks);
    }


    //    @GetMapping("/recommendBooksByTopGenre")
//    public ResponseEntity<List<Book>> recommendBooksByTopGenre(
//            Authentication authentication
//    ) {
//        if (authentication != null && authentication.isAuthenticated()) {
//            User user = (User) authentication.getPrincipal();
//            List<Book> recommendedBooks = service.recommendBooksByTopGenre(user.getId());
//            return ResponseEntity.ok(recommendedBooks);
//        } else {
//            // Handle case where user is not authenticated
//            throw new IllegalArgumentException("Connected user is null or not authenticated.");
//        }
//    }
    //tkhdm
    @GetMapping("/findUserGenres")
    public ResponseEntity<List<Genre>> findUserGenres(
            @RequestParam("ownerId") Integer ownerId
    ) {
        List<Genre> userGenres = service.findUserGenres(ownerId);
        return ResponseEntity.ok(userGenres);
    }

    @GetMapping("/findAllBooks")
    public ResponseEntity<PageResponse<BookResponse>> findAllBooks(

            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(service.findAllBooks(page, size, connectedUser));
    }
    @GetMapping("/all")
    public List<Book> getAll() {
        return service.getAll();
    }
    @GetMapping("/user/genres")
    public ResponseEntity<List<Genre>> getUserGenres(@RequestParam("ownerId") Integer ownerId) {
        List<Genre> userGenres = service.findUserGenres(ownerId);

        if (userGenres.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(userGenres);
    }
//    @GetMapping("/recommendBooksByTopGenre")
//    public ResponseEntity<List<Book>> recommendBooksByTopGenre(
//            Authentication connectedUser
//    ) {
//        if (connectedUser != null && connectedUser.isAuthenticated()) {
//            User user = (User) connectedUser.getPrincipal();
//            List<Book> recommendedBooks = service.recommendBooksByTopGenre(user.getId());
//            return ResponseEntity.ok(recommendedBooks);
//        } else {
//            // Handle case where user is not authenticated
//            throw new IllegalArgumentException("Connected user is null or not authenticated.");
//        }
//    }
//    @GetMapping("/recommendations")
//    public ResponseEntity<List<Book>> recommendBooksByTopGenre(Authentication authentication) {
//        List<Book> recommendedBooks = service.recommendBooksByTopGenre(authentication);
//        return new ResponseEntity<>(recommendedBooks, HttpStatus.OK);
//    }
//@GetMapping("/user/recommendations")
//public ResponseEntity<List<Book>> recommendBooksByTopGenre(Authentication authentication) {
//    List<Book> recommendedBooks = service.recommendBooksByTopGenre(authentication);
//    return ResponseEntity.ok().body(recommendedBooks);
//}


//    @PutMapping("/updateBook/{bookId}")
//    public ResponseEntity<String> updateBook(
//            @PathVariable Integer bookId,
//            @RequestBody BookRequest request,
//            @// Inject the current authenticated user
//    ) {
//        try {
//            // Call the service method to update the book
//            service.modifyBook(bookId, request, currentUser);
//            return ResponseEntity.ok("Book updated successfully");
//        } catch (EntityNotFoundException e) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No book found with ID: " + bookId);
//        } catch (SecurityException e) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to update this book");
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while updating the book");
//        }
//    }


    @PutMapping("/updateBook/{bookId}")
    public ResponseEntity<Book> modifyBook(
            @PathVariable Integer bookId,
            @RequestBody Book book,
            @AuthenticationPrincipal User currentUser // Inject the current authenticated user
    ) {
        try {
            // Set the id of the book to be updated
            book.setId(bookId);

            // Call the service method to update the book
            Book updatedBook = service.modifyBook(book, currentUser);
            return ResponseEntity.ok(updatedBook);
        } catch (SecurityException e) {
            // Handle security exception
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
    @GetMapping("/owner")
    public ResponseEntity<PageResponse<BookResponse>> findAllBooksByOwner(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(service.findAllBooksByOwner(page, size, connectedUser));
    }


    @GetMapping("/returned")
    public ResponseEntity<PageResponse<BorrowedBookResponse>> findAllReturnedBooks(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(service.findAllReturnedBooks(page, size, connectedUser));
    }

    @PatchMapping("/shareable/{book-id}")
    public ResponseEntity<Integer> updateShareableStatus(
            @PathVariable("book-id") Integer bookId,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(service.updateShareableStatus(bookId, connectedUser));
    }

    @PatchMapping("/archived/{book-id}")
    public ResponseEntity<Integer> updateArchivedStatus(
            @PathVariable("book-id") Integer bookId,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(service.updateArchivedStatus(bookId, connectedUser));
    }



    @PatchMapping("borrow/return/{book-id}")
    public ResponseEntity<Integer> returnBorrowBook(
            @PathVariable("book-id") Integer bookId,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(service.returnBorrowedBook(bookId, connectedUser));
    }

    @PatchMapping("borrow/return/approve/{book-id}")
    public ResponseEntity<Integer> approveReturnBorrowBook(
            @PathVariable("book-id") Integer bookId,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(service.approveReturnBorrowedBook(bookId, connectedUser));
    }

//    @PostMapping(value = "/cover/{book-id}", consumes = "multipart/form-data")
//    public ResponseEntity<?> uploadBookCoverPicture(
//            @PathVariable("book-id") Integer bookId,
//            @Parameter()
//            @RequestPart("file") MultipartFile file,
//            Authentication connectedUser
//    ) {
//        service.uploadBookCoverPicture(file, connectedUser, bookId);
//        return ResponseEntity.accepted().build();
//    }
}