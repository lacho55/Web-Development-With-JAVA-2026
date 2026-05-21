export enum Genre {
  DRAMA    = 'DRAMA',
  COMEDY   = 'COMEDY',
  MUSICAL  = 'MUSICAL',
  THRILLER = 'THRILLER',
  OPERA    = 'OPERA',
}

export enum AgeRating {
  ALL   = 'ALL',
  PG_12 = 'PG_12',
  PG_16 = 'PG_16',
  R_18  = 'R_18',
}

export interface Show {
  id: number;
  title: string;
  description: string;
  genre: Genre;
  durationMinutes: number;
  ageRating: AgeRating;
}
