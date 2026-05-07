package bg.uni.fmi.theatre.repository;

import bg.uni.fmi.theatre.domain.Show;
import bg.uni.fmi.theatre.vo.AgeRating;
import bg.uni.fmi.theatre.vo.Genre;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1)
public class ShowSeeder implements CommandLineRunner {

    private final ShowRepository shows;

    public ShowSeeder(ShowRepository shows) {
        this.shows = shows;
    }

    @Override
    public void run(String... args) {
        if (shows.count() > 0) return;

        shows.save(new Show("Hamlet",
                "William Shakespeare's timeless tragedy set in the royal court of Denmark. "
                + "Prince Hamlet is haunted by his father's ghost, who reveals he was murdered by Hamlet's uncle Claudius. "
                + "Torn between duty and doubt, Hamlet spirals into a world of deception, madness, and revenge. "
                + "Featuring iconic soliloquies including 'To be or not to be', this production uses a minimalist "
                + "stage design with dramatic lighting to bring Shakespeare's psychological masterpiece to life.",
                Genre.DRAMA, 180, AgeRating.PG_16));

        shows.save(new Show("Chicago",
                "Set in the jazz age of the 1920s, Chicago tells the story of Roxie Hart, a housewife who murders "
                + "her lover and is arrested alongside the nightclub star Velma Kelly. Together they navigate the "
                + "corrupt criminal justice system, manipulating the media to become celebrities. With iconic numbers "
                + "like 'All That Jazz', 'Cell Block Tango', and 'Razzle Dazzle', this production features dazzling "
                + "choreography inspired by Bob Fosse's original vision. A biting satire of fame, justice, and the media.",
                Genre.MUSICAL, 135, AgeRating.PG_12));

        shows.save(new Show("A Midsummer Night's Dream",
                "Shakespeare's most enchanting comedy follows four young lovers who flee into a magical forest "
                + "outside Athens, where mischievous fairies led by Oberon and Puck wreak havoc with a love potion. "
                + "Meanwhile, a troupe of amateur actors rehearses a play-within-a-play, leading to hilarious mix-ups. "
                + "This family-friendly production combines whimsical puppetry, live music, and colourful costumes "
                + "to create a joyful celebration of love, imagination, and the absurdity of human emotions.",
                Genre.COMEDY, 110, AgeRating.ALL));

        shows.save(new Show("The Phantom of the Opera",
                "Andrew Lloyd Webber's legendary musical tells the story of a mysterious masked genius who haunts "
                + "the Paris Opera House and falls obsessively in love with Christine, a young soprano. As the Phantom "
                + "tutors her voice from the shadows, a love triangle emerges with the dashing Raoul. Featuring the "
                + "iconic chandelier crash, an underground lake, and unforgettable songs like 'The Music of the Night' "
                + "and 'All I Ask of You', this grand production combines gothic romance with spectacular staging.",
                Genre.MUSICAL, 150, AgeRating.PG_12));

        shows.save(new Show("Swan Lake",
                "Tchaikovsky's beloved ballet tells the story of Prince Siegfried, who falls in love with Odette, "
                + "a princess cursed to live as a swan by the evil sorcerer Rothbart. At a royal ball, Siegfried is "
                + "tricked by Rothbart's daughter Odile, disguised as a black swan, into breaking his vow of love. "
                + "This classical production features stunning corps de ballet formations, virtuoso pas de deux, and "
                + "Tchaikovsky's soaring orchestral score performed live. A timeless story of love, betrayal, and sacrifice.",
                Genre.BALLET, 140, AgeRating.ALL));
    }
}