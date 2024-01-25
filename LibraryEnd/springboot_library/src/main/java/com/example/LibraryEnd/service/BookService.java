package com.example.LibraryEnd.service;

import com.example.LibraryEnd.repository.BookRepository;
import com.example.LibraryEnd.repository.CheckoutRepository;
import com.example.LibraryEnd.repository.HistoryRepository;
import com.example.LibraryEnd.entity.Book;
import com.example.LibraryEnd.entity.Checkout;
import com.example.LibraryEnd.entity.History;
import com.example.LibraryEnd.responsemodels.ShelfCurrentLoansResponse;
import com.example.LibraryEnd.personel.User;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.time.DayOfWeek;
import java.time.temporal.ChronoUnit;

@Service
@Transactional
public class BookService {


    private final BookRepository bookRepository;

    private final CheckoutRepository checkoutRepository;

    private final HistoryRepository historyRepository;

    public BookService(BookRepository bookRepository, CheckoutRepository checkoutRepository,
                       HistoryRepository historyRepository) {
        this.bookRepository = bookRepository;
        this.checkoutRepository = checkoutRepository;
        this.historyRepository = historyRepository;
    }

    public Book checkoutBook (String userEmail, Long bookId) throws Exception {

        Optional<Book> book = bookRepository.findById(bookId);

        Checkout validateCheckout = checkoutRepository.findByUserEmailAndBookId(userEmail, bookId); //belirli bir kitabı daha önce ödünç alıp almadığına bakar

        if (!book.isPresent() || validateCheckout != null || book.get().getCopiesAvailable() <= 0) {
            //kitap mevcut değilse, kullanıcı kitabı daha önce ödünç almışsa veya mevcut kopya yoksa, bir istisna (exception) fırlatılır ve metot çalışmayı durdurur.
            throw new Exception("Book doesn't exist or already checked out by user");
        }

        book.get().setCopiesAvailable(book.get().getCopiesAvailable() - 1); //Bu satır, kitabın mevcut kopya sayısını bir azaltarak, kullanıcının kitabı ödünç aldığını temsil eder.
        bookRepository.save(book.get()); // değiştirilmiş kitap nesnesini veritabanında günceller.

        Checkout checkout = new Checkout( //kullanıcının, ödünç alınan kitap için bir kontrol kaydıdır. Bu kaydın başlangıç ve bitiş tarihleri de belirtilmiştir.
                userEmail,
                LocalDate.now().toString(),
                LocalDate.now().plusDays(7).toString(), // bura değişecek----------------
                book.get().getId()
        );

        checkoutRepository.save(checkout);

        return book.get();
    }

    public Boolean checkoutBookByUser(String userEmail, Long bookId) {
        Checkout validateCheckout = checkoutRepository.findByUserEmailAndBookId(userEmail, bookId);
        if (validateCheckout != null) {
            return true; // kullanıcı ödünç almış
        } else {
            return false;  // kullanıcı ödünç almamış
        }
    }

    public int currentLoansCount(String userEmail) {
        return checkoutRepository.findBooksByUserEmail(userEmail).size();
    }

    public List<ShelfCurrentLoansResponse> currentLoans(String userEmail) throws Exception {

        List<ShelfCurrentLoansResponse> shelfCurrentLoansResponses = new ArrayList<>(); // kullanıcının şuan aldığı kitap bilgileri

        List<Checkout> checkoutList = checkoutRepository.findBooksByUserEmail(userEmail);
        //Kullanıcının userEmail (kullanıcı e-posta adresi) ile eşleşen ödünç alınmış kitaplarının listesi checkoutList değişkenine atanır.
        List<Long> bookIdList = new ArrayList<>(); //  Kitap kimliklerini tutmak için boş bir Long listesi oluşturulur.

        for (Checkout i: checkoutList) {  //Her bir ödünç alınmış kitabın kimliği (getBookId()) bookIdList listesine ekle
            bookIdList.add(i.getBookId());
        }

        List<Book> books = bookRepository.findBooksByBookIds(bookIdList);  //içindeki kitap kimliklerine göre ilgili kitapların listesi books değişkenine atanır.

        //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");  // Tarih formatı belirlenir.

        for (Book book : books) {
            Optional<Checkout> checkout = checkoutList.stream()  //: Kitabın ödünç alınma bilgilerini içeren bir Optional<Checkout> oluşturulur.
                    .filter(x -> x.getBookId() == book.getId()).findFirst();

            if (checkout.isPresent()) { // ödünç alınmışsa

                LocalDate returnDate = LocalDate.parse(checkout.get().getReturnDate());
                LocalDate currentDate = LocalDate.now();

                //-------------------------
                if (isWeekend(returnDate)) {
                    returnDate = moveReturnDateToNextWeekday(returnDate);
                }
                //-------------------------
                long difference_In_Days;

                // Kullanıcının türüne göre ödünç gün sayısını belirle
                if (userEmail == "S" ) {
                    difference_In_Days = ChronoUnit.DAYS.between(currentDate, returnDate) + 20;
                } else if (userEmail == "A" ) {
                    difference_In_Days = ChronoUnit.DAYS.between(currentDate, returnDate) + 30;
                }


                else {
                    // Diğer türler için varsayılan değer
                    difference_In_Days = ChronoUnit.DAYS.between(currentDate, returnDate);
                }

                shelfCurrentLoansResponses.add(new ShelfCurrentLoansResponse(book, (int) difference_In_Days));
                // alınan ödünç kitapların bilgilerini ve iade tarihlerini kaç gün kaldığını içerir.
            }
        }
        return shelfCurrentLoansResponses;
    }
    
    //---------------------------------
    private boolean isWeekend(LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        return dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY;
    }

    private LocalDate moveReturnDateToNextWeekday(LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();

        // Eğer Cumartesi ise 2 gün ekleyelim, Pazar ise 1 gün ekleyelim
        int daysToAdd = (dayOfWeek == DayOfWeek.SATURDAY) ? 2 : 1;

        return date.plusDays(daysToAdd);
    }
    //---------------------------------
    

    public void returnBook (String userEmail, Long bookId) throws Exception {

        Optional<Book> book = bookRepository.findById(bookId);

        Checkout validateCheckout = checkoutRepository.findByUserEmailAndBookId(userEmail, bookId);
        //Kullanıcının ve bookıd ödünç alınmış kitaplar arasında olup olmadığını kontrol etmek için checkoutRepository üzerinden ilgili Checkout nesnesini alır.

        if (!book.isPresent() || validateCheckout == null) { // kitap bulunmazsa veya ödünç alınmış kitap bilgisi bulunmazsa hata verir
            throw new Exception("Book does not exist or not checked out by user");
        }

        book.get().setCopiesAvailable(book.get().getCopiesAvailable() + 1); // Kitabın mevcut kopya sayısı bir artırılır.

        bookRepository.save(book.get()); // veriler güncelelnir
        checkoutRepository.deleteById(validateCheckout.getId()); // ödünç alma kaydı veritabanından silinir.

        History history = new History(
                userEmail,
                validateCheckout.getCheckoutDate(),
                LocalDate.now().toString(),
                book.get().getTitle(),
                book.get().getAuthor(),
                book.get().getDescription(),
                book.get().getImg()
        );

        historyRepository.save(history);
    }

    public void renewLoan(String userEmail, Long bookId) throws Exception {

        Checkout validateCheckout = checkoutRepository.findByUserEmailAndBookId(userEmail, bookId);
        // kullanıcın ve bookıd nin ödünç alınıp alınmadığına bakar


        if (validateCheckout == null) {
            //nesnesi null ise (yani, kullanıcı tarafından ödünç alınmış bir kitap kaydı yoksa) bir hata verdirirr
            throw new Exception("Book does not exist or not checked out by user");
        }

        SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date d1 = sdFormat.parse(validateCheckout.getReturnDate()); // kitabın önceki iade tarihi
        Date d2 = sdFormat.parse(LocalDate.now().toString()); // şuanki tarihi

        if (d1.compareTo(d2) > 0 || d1.compareTo(d2) == 0) {  //Eğer kitabın önceki iade tarihi şu anki tarihten büyükse veya eşitse, iade tarihi uzatılır.
            validateCheckout.setReturnDate(LocalDate.now().plusDays(7).toString()); // Kitabın yeni iade tarihi, şu anki tarihe 7 gün eklenmiş hali olarak ayarlanır.
            checkoutRepository.save(validateCheckout); //Değişiklikler veritabanına kaydedilir.
        }
    }

}