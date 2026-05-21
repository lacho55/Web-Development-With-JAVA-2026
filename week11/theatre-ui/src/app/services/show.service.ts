import { Injectable, signal, computed } from '@angular/core';
import { Show, Genre, AgeRating } from '../models/show.model';

@Injectable({ providedIn: 'root' })
export class ShowService {

  private nextId = 6;

  private readonly _shows = signal<Show[]>([
    {
      id: 1, title: 'Hamlet',
      description: "William Shakespeare's timeless tragedy of a Danish prince seeking revenge.",
      genre: Genre.DRAMA, durationMinutes: 180, ageRating: AgeRating.PG_16,
    },
    {
      id: 2, title: 'Chicago',
      description: 'Set in the jazz age of the 1920s — murder, greed, corruption and show business.',
      genre: Genre.MUSICAL, durationMinutes: 135, ageRating: AgeRating.PG_12,
    },
    {
      id: 3, title: 'The Mousetrap',
      description: "Agatha Christie's legendary whodunit — the longest-running play in history.",
      genre: Genre.THRILLER, durationMinutes: 120, ageRating: AgeRating.ALL,
    },
    {
      id: 4, title: 'La Traviata',
      description: "Verdi's opera about love, sacrifice, and society in 19th-century Paris.",
      genre: Genre.OPERA, durationMinutes: 150, ageRating: AgeRating.ALL,
    },
    {
      id: 5, title: 'Waiting for Godot',
      description: 'Two characters wait indefinitely for someone named Godot who never arrives.',
      genre: Genre.DRAMA, durationMinutes: 110, ageRating: AgeRating.PG_16,
    },
  ]);

  readonly shows = computed(() => this._shows());

  getById(id: number): Show | undefined {
    return this._shows().find(s => s.id === id);
  }

  create(show: Omit<Show, 'id'>): Show {
    const newShow: Show = { ...show, id: this.nextId++ };
    this._shows.update(list => [...list, newShow]);
    return newShow;
  }

  update(id: number, show: Omit<Show, 'id'>): Show | undefined {
    let updated: Show | undefined;
    this._shows.update(list =>
      list.map(s => {
        if (s.id === id) { updated = { ...show, id }; return updated; }
        return s;
      })
    );
    return updated;
  }

  delete(id: number): void {
    this._shows.update(list => list.filter(s => s.id !== id));
  }
}
