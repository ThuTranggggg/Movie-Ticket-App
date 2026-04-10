package com.movieticketapp.firebase;

import com.google.firebase.firestore.WriteBatch;
import com.movieticketapp.models.Movie;
import com.movieticketapp.models.Showtime;
import com.movieticketapp.models.Theater;
import com.movieticketapp.utils.DateTimeUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FirestoreSeeder {

    public void seedDemoData(DataCallback<String> callback) {
        WriteBatch batch = FirebaseManager.getFirestore().batch();
        List<String> seats = createSeats();

        List<Movie> movies = Arrays.asList(
                new Movie("movie_dune", "Dune: Part Two", "Sci-Fi", "166", "8.8", "Paul Atreides unites with the Fremen while navigating prophecy, revenge and a war that could reshape the galaxy.", "https://images.unsplash.com/photo-1517604931442-7e0c8ed2963c?auto=format&fit=crop&w=800&q=80", DateTimeUtils.getFutureIsoDate(1, 0, 0), true),
                new Movie("movie_interstellar", "Interstellar", "Sci-Fi", "169", "8.9", "A team of explorers travel through a wormhole in space to secure humanity's future beyond Earth.", "https://images.unsplash.com/photo-1489599849927-2ee91cede3ba?auto=format&fit=crop&w=800&q=80", DateTimeUtils.getFutureIsoDate(2, 0, 0), true),
                new Movie("movie_batman", "The Batman", "Action", "176", "8.2", "Batman uncovers corruption in Gotham while tracking a ruthless serial killer leaving cryptic clues behind.", "https://images.unsplash.com/photo-1536440136628-849c177e76a1?auto=format&fit=crop&w=800&q=80", DateTimeUtils.getFutureIsoDate(3, 0, 0), true),
                new Movie("movie_inception", "Inception", "Thriller", "148", "8.7", "A skilled thief dives into layered dream worlds to plant an idea in the mind of a powerful target.", "https://images.unsplash.com/photo-1440404653325-ab127d49abc1?auto=format&fit=crop&w=800&q=80", DateTimeUtils.getFutureIsoDate(4, 0, 0), true),
                new Movie("movie_avengers", "Avengers: Endgame", "Superhero", "181", "8.4", "The Avengers assemble for one final mission to restore the universe after the devastating snap.", "https://images.unsplash.com/photo-1460881680858-30d872d5b530?auto=format&fit=crop&w=800&q=80", DateTimeUtils.getFutureIsoDate(5, 0, 0), true),
                new Movie("movie_lalaland", "La La Land", "Romance", "128", "8.0", "A pianist and an aspiring actress chase their dreams in Los Angeles while falling in love.", "https://images.unsplash.com/photo-1502134249126-9f3755a50d78?auto=format&fit=crop&w=800&q=80", DateTimeUtils.getFutureIsoDate(6, 0, 0), true)
        );

        List<Theater> theaters = Arrays.asList(
                new Theater("theater_cgv_batrieu", "CGV Vincom Ba Trieu", "191 Ba Trieu, Hai Ba Trung", "Ha Noi", true),
                new Theater("theater_galaxy_mipec", "Galaxy Mipec Long Bien", "2 Long Bien 2, Ngoc Lam, Long Bien", "Ha Noi", true),
                new Theater("theater_lotte_tayho", "Lotte Cinema Tay Ho", "683 Lac Long Quan, Tay Ho", "Ha Noi", true),
                new Theater("theater_beta_mydinh", "Beta Cinemas My Dinh", "Me Tri, Nam Tu Liem", "Ha Noi", true),
                new Theater("theater_bhd_phamngoc", "BHD Star Pham Ngoc Thach", "8 Pham Ngoc Thach, Dong Da", "Ha Noi", true),
                new Theater("theater_cgv_tran_duy", "CGV Tran Duy Hung", "119 Tran Duy Hung, Cau Giay", "Ha Noi", true)
        );

        for (Movie movie : movies) {
            batch.set(FirebaseManager.getFirestore().collection("movies").document(movie.getMovieId()), movie.toMap());
        }

        for (Theater theater : theaters) {
            batch.set(FirebaseManager.getFirestore().collection("theaters").document(theater.getTheaterId()), theater.toMap());
        }

        int showtimeCounter = 1;
        int[] hourOptions = {10, 13, 16, 19, 21};
        for (int dayOffset = 0; dayOffset < 7; dayOffset++) {
            for (int movieIndex = 0; movieIndex < movies.size(); movieIndex++) {
                Movie movie = movies.get(movieIndex);
                int durationMinutes = parseDuration(movie.getDuration());
                for (int theaterIndex = 0; theaterIndex < theaters.size(); theaterIndex++) {
                    Theater theater = theaters.get(theaterIndex);
                    int startHour = hourOptions[(movieIndex + theaterIndex + dayOffset) % hourOptions.length];
                    int startMinute = ((movieIndex * 10) + (theaterIndex * 5)) % 60;
                    int endHour = startHour + (durationMinutes / 60);
                    int endMinute = startMinute + (durationMinutes % 60);
                    if (endMinute >= 60) {
                        endHour += 1;
                        endMinute -= 60;
                    }
                    String showtimeId = "showtime_" + showtimeCounter++;
                    String roomName = "Screen " + ((theaterIndex + movieIndex) % 8 + 1);
                    double price = 9.5 + (movieIndex * 0.8) + (theaterIndex * 0.4);
                    List<String> bookedSeats = createBookedSeats(movieIndex, theaterIndex, dayOffset);
                    Showtime showtime = new Showtime(
                            showtimeId,
                            movie.getMovieId(),
                            theater.getTheaterId(),
                            DateTimeUtils.getFutureIsoDate(dayOffset, startHour, startMinute),
                            DateTimeUtils.getFutureIsoDate(dayOffset, endHour, endMinute),
                            roomName,
                            price,
                            new ArrayList<>(seats),
                            bookedSeats
                    );
                    batch.set(FirebaseManager.getFirestore().collection("showtimes").document(showtime.getShowtimeId()), showtime.toMap());
                }
            }
        }

        batch.commit()
                .addOnSuccessListener(unused -> callback.onSuccess("Demo data inserted successfully."))
                .addOnFailureListener(e -> callback.onError(e.getMessage() == null ? "Unable to seed Firestore demo data." : e.getMessage()));
    }

    private List<String> createSeats() {
        List<String> seats = new ArrayList<>();
        char[] rows = {'A', 'B', 'C', 'D', 'E'};
        for (char row : rows) {
            for (int column = 1; column <= 6; column++) {
                seats.add(row + String.valueOf(column));
            }
        }
        return seats;
    }

    private List<String> createBookedSeats(int movieIndex, int theaterIndex, int dayOffset) {
        List<String> bookedSeats = new ArrayList<>();
        String[] samples = {"A1", "A2", "B3", "C4", "D2", "E5"};
        int count = (movieIndex + theaterIndex + dayOffset) % 3;
        for (int i = 0; i < count; i++) {
            bookedSeats.add(samples[(movieIndex + theaterIndex + dayOffset + i) % samples.length]);
        }
        return bookedSeats;
    }

    private int parseDuration(String duration) {
        try {
            return Integer.parseInt(duration);
        } catch (NumberFormatException exception) {
            return 120;
        }
    }
}
