# Angular UI (Theatre Ticketing) — Exam 1

For this exam you will build the frontend of the Theatre Ticketing system using **Angular 21**.  
The backend is **not required** — all data lives in a local service that mimics the API contract.  
Everything lives on a **single page** — no routing. Navigation is controlled by a `currentView`
signal in `AppComponent`, which conditionally renders one of three dedicated child components.

---

## Prerequisites

- Node.js ≥ 20 and npm installed
- Angular CLI 21 installed globally: `npm install -g @angular/cli@21`
- Basic knowledge of Angular standalone components, services, and signals

---

## Project structure you will build

```
theatre-ui/
├── src/
│   └── app/
│       ├── models/
│       │   └── show.model.ts
│       ├── services/
│       │   └── show.service.ts
│       ├── components/
│       │   ├── show-list/
│       │   │   ├── show-list.component.ts
│       │   │   └── show-list.component.html
│       │   ├── show-detail/
│       │   │   ├── show-detail.component.ts
│       │   │   └── show-detail.component.html
│       │   └── show-form/
│       │       ├── show-form.component.ts
│       │       └── show-form.component.html
│       ├── app.component.ts
│       └── app.component.html
```

---

## Task 0 — Bootstrap the Angular project

Generate a new Angular 21 project (standalone, **no routing**, no SSR):

```bash
ng new theatre-ui --routing=false --style=css --standalone
cd theatre-ui
ng serve
```

Open `http://localhost:4200` and confirm the default Angular page loads.

> **Note:** Angular 21 projects are **zoneless by default** — `zone.js` is no longer listed in
> `polyfills`. Change detection is driven entirely by signals. Do not add `zone.js` back.

---

## Task 1 — Define the domain model

### 1.1 Create the enums

Create `src/app/models/show.model.ts`:

```ts
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
```

### 1.2 Create the Show interface

In the same file add:

```ts
export interface Show {
  id: number;
  title: string;
  description: string;
  genre: Genre;
  durationMinutes: number;
  ageRating: AgeRating;
}
```

> Using an `interface` instead of a `class` is idiomatic Angular for plain data shapes.

---

## Task 2 — Create the ShowService with mock data

Generate the service:

```bash
ng generate service services/show
```

Open `src/app/services/show.service.ts` and replace its content with the mock implementation below.  
This service **simulates** the REST API — no HTTP calls are made yet.

In Angular 21, use `signal()` to hold mutable state and `computed()` to derive read-only views of it.

```ts
import { Injectable, signal, computed } from '@angular/core';
import { Show, Genre, AgeRating } from '../models/show.model';

@Injectable({ providedIn: 'root' })
export class ShowService {

  private nextId = 6;

  // The master list is held in a writable signal.
  // Components read the computed() projection — they never mutate the list directly.
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

  // Public read-only projection — components bind to this.
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
```

> These methods mirror the REST contract (`GET /shows`, `GET /shows/{id}`, `POST /shows`,
> `PUT /shows/{id}`, `DELETE /shows/{id}`). When you connect the real backend later, only this
> service needs to change.

---

## Task 3 — ShowListComponent

Generate the component:

```bash
ng generate component components/show-list --standalone
```

This component is responsible for **displaying all shows** and letting the user trigger navigation
to the detail or form views. It does **not** navigate itself — it fires output events and lets
`AppComponent` decide what to show next.

### 3.1 Component class

`show-list.component.ts`:

```ts
import { Component, inject, output } from '@angular/core';
import { ShowService } from '../../services/show.service';
import { Show } from '../../models/show.model';

@Component({
  selector: 'app-show-list',
  standalone: true,
  templateUrl: './show-list.component.html',
  styleUrl: './show-list.component.css',
})
export class ShowListComponent {

  private readonly showService = inject(ShowService);

  // Read-only signal derived straight from the service — auto-updates on any mutation.
  readonly shows = this.showService.shows;

  // Output events — AppComponent listens to these and switches the active view.
  readonly showSelected  = output<Show>();    // user clicked a show title → go to detail
  readonly editRequested = output<Show>();    // user clicked Edit → go to form in edit mode
  readonly addRequested  = output<void>();    // user clicked Add Show → go to form in create mode

  deleteShow(id: number): void {
    if (confirm('Are you sure you want to delete this show?')) {
      this.showService.delete(id);
      // No navigation needed — the signal update re-renders the table automatically.
    }
  }
}
```

> In Angular 21 the `output()` function replaces the `@Output() EventEmitter` decorator.
> It is lighter, fully typed, and integrates naturally with signals.

### 3.2 Template

`show-list.component.html`:

```html
<div class="toolbar">
  <h2>Shows</h2>
  <button (click)="addRequested.emit()">+ Add Show</button>
</div>

<table>
  <thead>
    <tr>
      <th>Title</th>
      <th>Genre</th>
      <th>Duration (min)</th>
      <th>Age Rating</th>
      <th>Actions</th>
    </tr>
  </thead>
  <tbody>
    @for (show of shows(); track show.id) {
      <tr>
        <td>
          <a (click)="showSelected.emit(show)" style="cursor:pointer">{{ show.title }}</a>
        </td>
        <td>{{ show.genre }}</td>
        <td>{{ show.durationMinutes }}</td>
        <td>{{ show.ageRating }}</td>
        <td>
          <button (click)="editRequested.emit(show)">Edit</button>
          <button (click)="deleteShow(show.id)">Delete</button>
        </td>
      </tr>
    } @empty {
      <tr><td colspan="5">No shows found.</td></tr>
    }
  </tbody>
</table>
```

> `@for` requires a `track` expression. Use `show.id` — it is unique and stable, so Angular
> can efficiently reconcile DOM nodes when the list changes.

---

## Task 4 — ShowDetailComponent

Generate the component:

```bash
ng generate component components/show-detail --standalone
```

This component displays the full details of a single show. The show to display is passed in as
a signal input from `AppComponent`.

### 4.1 Component class

`show-detail.component.ts`:

```ts
import { Component, input, output } from '@angular/core';
import { Show } from '../../models/show.model';

@Component({
  selector: 'app-show-detail',
  standalone: true,
  templateUrl: './show-detail.component.html',
  styleUrl: './show-detail.component.css',
})
export class ShowDetailComponent {

  // Signal input — AppComponent passes the selected show down.
  readonly show = input<Show | undefined>(undefined);

  // Output events
  readonly backRequested  = output<void>();   // user clicked Back → go to list
  readonly editRequested  = output<Show>();   // user clicked Edit → go to form in edit mode
}
```

> In Angular 21 the `input()` function replaces the `@Input()` decorator. The value is available
> as a signal: call `this.show()` in the class or `show()` in the template.

### 4.2 Template

`show-detail.component.html`:

```html
<button (click)="backRequested.emit()">← Back to list</button>

@if (show(); as s) {
  <h2>{{ s.title }}</h2>
  <p><strong>Genre:</strong> {{ s.genre }}</p>
  <p><strong>Duration:</strong> {{ s.durationMinutes }} min</p>
  <p><strong>Age Rating:</strong> {{ s.ageRating }}</p>
  <p><strong>Description:</strong> {{ s.description }}</p>
  <button (click)="editRequested.emit(s)">Edit</button>
} @else {
  <p>Show not found.</p>
}
```

> `@if (show(); as s)` unwraps the signal value and gives it a local alias `s` — no need to
> repeat `show()` for every binding inside the block.

---

## Task 5 — ShowFormComponent

Generate the component:

```bash
ng generate component components/show-form --standalone
```

This single component handles both **create** and **edit**. The mode and the pre-populated data
are passed in from `AppComponent` via signal inputs.

### 5.1 Component class

`show-form.component.ts`:

```ts
import { Component, input, output, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { inject } from '@angular/core';
import { ShowService } from '../../services/show.service';
import { Show, Genre, AgeRating } from '../../models/show.model';

@Component({
  selector: 'app-show-form',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './show-form.component.html',
  styleUrl: './show-form.component.css',
})
export class ShowFormComponent implements OnInit {

  private readonly showService = inject(ShowService);

  // Signal inputs from AppComponent
  readonly editShow = input<Show | undefined>(undefined);   // undefined → create mode

  // Output events
  readonly saved      = output<Show>();   // emits the created/updated show → go to detail
  readonly cancelled  = output<void>();   // user clicked Cancel → go to list

  readonly genres     = Object.values(Genre);
  readonly ageRatings = Object.values(AgeRating);

  formData = {
    title: '',
    description: '',
    genre: Genre.DRAMA,
    durationMinutes: 90,
    ageRating: AgeRating.ALL,
  };

  get isEditMode(): boolean {
    return this.editShow() !== undefined;
  }

  ngOnInit(): void {
    const show = this.editShow();
    if (show) {
      // Pre-populate form when editing
      this.formData = {
        title: show.title,
        description: show.description,
        genre: show.genre,
        durationMinutes: show.durationMinutes,
        ageRating: show.ageRating,
      };
    }
  }

  onSubmit(isValid: boolean | null): void {
    if (!isValid) return;

    const show = this.editShow();
    if (show) {
      const updated = this.showService.update(show.id, this.formData);
      if (updated) this.saved.emit(updated);
    } else {
      const created = this.showService.create(this.formData);
      this.saved.emit(created);
    }
  }
}
```

### 5.2 Template

`show-form.component.html`:

```html
<h2>{{ isEditMode ? 'Edit Show' : 'Add Show' }}</h2>

<form #showForm="ngForm" (ngSubmit)="onSubmit(showForm.valid)">

  <label>Title *
    <input name="title" [(ngModel)]="formData.title"
           required maxlength="100" #titleField="ngModel" />
    @if (titleField.invalid && titleField.touched) {
      <span class="error">Title is required (max 100 chars).</span>
    }
  </label>

  <label>Description
    <textarea name="description" [(ngModel)]="formData.description"></textarea>
  </label>

  <label>Genre *
    <select name="genre" [(ngModel)]="formData.genre" required>
      @for (g of genres; track g) {
        <option [value]="g">{{ g }}</option>
      }
    </select>
  </label>

  <label>Duration (minutes) *
    <input name="durationMinutes" type="number"
           [(ngModel)]="formData.durationMinutes"
           required min="1" #durField="ngModel" />
    @if (durField.invalid && durField.touched) {
      <span class="error">Duration must be at least 1 minute.</span>
    }
  </label>

  <label>Age Rating *
    <select name="ageRating" [(ngModel)]="formData.ageRating" required>
      @for (r of ageRatings; track r) {
        <option [value]="r">{{ r }}</option>
      }
    </select>
  </label>

  <div class="form-actions">
    <button type="submit">{{ isEditMode ? 'Save Changes' : 'Create Show' }}</button>
    <button type="button" (click)="cancelled.emit()">Cancel</button>
  </div>

</form>
```

---

## Task 6 — Wire everything in AppComponent

`AppComponent` is the **orchestrator**: it owns the navigation state and passes data down to child
components via inputs, listening for output events to switch views.

### 6.1 Component class

`app.component.ts`:

```ts
import { Component, signal, inject } from '@angular/core';
import { ShowListComponent }   from './components/show-list/show-list.component';
import { ShowDetailComponent } from './components/show-detail/show-detail.component';
import { ShowFormComponent }   from './components/show-form/show-form.component';
import { Show } from './models/show.model';

type View = 'list' | 'detail' | 'form';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [ShowListComponent, ShowDetailComponent, ShowFormComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css',
})
export class AppComponent {

  // ── navigation state ─────────────────────────────────────────────────────
  readonly currentView  = signal<View>('list');

  // ── data passed down to child components ─────────────────────────────────
  readonly selectedShow = signal<Show | undefined>(undefined);
  readonly showToEdit   = signal<Show | undefined>(undefined);

  // ── handlers for child component output events ───────────────────────────

  onShowSelected(show: Show): void {
    this.selectedShow.set(show);
    this.currentView.set('detail');
  }

  onEditRequested(show: Show): void {
    this.showToEdit.set(show);
    this.currentView.set('form');
  }

  onAddRequested(): void {
    this.showToEdit.set(undefined);   // undefined → create mode in the form
    this.currentView.set('form');
  }

  onSaved(show: Show): void {
    this.selectedShow.set(show);
    this.currentView.set('detail');
  }

  onCancelled(): void {
    this.currentView.set('list');
  }

  onBackRequested(): void {
    this.currentView.set('list');
  }
}
```

### 6.2 Template

`app.component.html`:

```html
<div class="container">

  @if (currentView() === 'list') {
    <app-show-list
      (showSelected)="onShowSelected($event)"
      (editRequested)="onEditRequested($event)"
      (addRequested)="onAddRequested()"
    />
  }

  @if (currentView() === 'detail') {
    <app-show-detail
      [show]="selectedShow()"
      (backRequested)="onBackRequested()"
      (editRequested)="onEditRequested($event)"
    />
  }

  @if (currentView() === 'form') {
    <app-show-form
      [editShow]="showToEdit()"
      (saved)="onSaved($event)"
      (cancelled)="onCancelled()"
    />
  }

</div>
```

> Each child component is only rendered when it is the active view. Switching views destroys
> the previous component and creates the new one — `ShowFormComponent.ngOnInit` therefore always
> runs with fresh inputs when the form opens.

---

## Task 7 — Minimal styling

Add global styles to `src/styles.css`:

```css
* { box-sizing: border-box; }

body {
  font-family: sans-serif;
  margin: 0;
  padding: 0;
  background: #f5f5f5;
}

.container {
  max-width: 900px;
  margin: 2rem auto;
  padding: 0 1rem;
}

app-show-list,
app-show-detail,
app-show-form {
  display: block;
  background: #fff;
  border-radius: 6px;
  padding: 1.5rem;
  box-shadow: 0 1px 4px rgba(0,0,0,.1);
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
}

table { width: 100%; border-collapse: collapse; }
th, td { padding: .5rem .75rem; border-bottom: 1px solid #ddd; text-align: left; }

label { display: block; margin-bottom: .75rem; }
input, select, textarea { display: block; width: 100%; margin-top: .25rem; padding: .4rem; }

.form-actions { display: flex; gap: .5rem; margin-top: 1rem; }
.error { color: #c00; font-size: .85rem; }
```

---

## Task 8 — Component communication diagram

Before running the app, verify that you understand the data flow:

```
AppComponent  (owns currentView, selectedShow, showToEdit)
│
├── <app-show-list>
│     outputs:  showSelected(show) ──────► onShowSelected   → currentView = 'detail'
│               editRequested(show) ─────► onEditRequested  → currentView = 'form'
│               addRequested() ──────────► onAddRequested   → currentView = 'form'
│
├── <app-show-detail>   [show]="selectedShow()"
│     outputs:  backRequested() ─────────► onBackRequested  → currentView = 'list'
│               editRequested(show) ─────► onEditRequested  → currentView = 'form'
│
└── <app-show-form>     [editShow]="showToEdit()"
      outputs:  saved(show) ──────────────► onSaved         → currentView = 'detail'
                cancelled() ────────────────► onCancelled   → currentView = 'list'
```

Child components **never** change the view themselves — they only emit events. `AppComponent`
is the single place where navigation decisions are made.

---

## Task 9 — Manual end-to-end test

Run the app (`ng serve`) and verify the following flows:

| # | Action | Expected result |
|---|--------|-----------------|
| 1 | App loads | 5 seeded shows appear in the table |
| 2 | Click a show title | `ShowDetailComponent` renders with all fields |
| 3 | Click "← Back to list" | `ShowListComponent` re-renders |
| 4 | Click "Edit" on a row | `ShowFormComponent` opens pre-populated with that show's values |
| 5 | Change the title and click "Save Changes" | Detail view shows the updated title |
| 6 | From detail, click "Edit" then "Cancel" | Returns to the list without changes |
| 7 | Click "+ Add Show", fill the form, submit | New show appears at the bottom of the list |
| 8 | Click "Delete" on a show | Confirmation prompt; show disappears from the list |
| 9 | Submit the form with an empty title | Validation error is displayed; submit is blocked |

---

## Task 10 — Homework (preparation for Exam 2)

Once the single-page UI is working with mock data, the next step is:

- Add `provideHttpClient()` in `app.config.ts`
- Replace the mock `ShowService` methods with real `HttpClient` calls returning `Observable<Show[]>` etc.
- Use `toSignal()` from `@angular/core/rxjs-interop` to bridge Observables into signals
- Handle HTTP errors (show an inline error message when a request fails)
- Add a loading indicator using a `signal<boolean>(false)` flag toggled before and after each call
- Introduce Angular routing so each view has its own URL — this will be the starting point for **Exam 2**
