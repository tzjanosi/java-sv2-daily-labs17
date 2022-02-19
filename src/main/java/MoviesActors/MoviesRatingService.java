package MoviesActors;

import java.util.Arrays;
import java.util.Optional;

public class MoviesRatingService {
    private MoviesRepository moviesRepository;
    private RatingRepository ratingRepository;

    public MoviesRatingService(MoviesRepository moviesRepository, RatingRepository ratingRepository) {
        this.moviesRepository = moviesRepository;
        this.ratingRepository = ratingRepository;
    }

    public void addRating(Movie movie, Integer... ratings){
        Optional<Movie> foundMovie=moviesRepository.findMovieByNameAndReleaseDate(movie);
        if(foundMovie.isPresent()){
            ratingRepository.insertRatings(foundMovie.get(), Arrays.asList(ratings));
            ratingRepository.updateRatingsAverage(foundMovie.get());
        }
    }
}
