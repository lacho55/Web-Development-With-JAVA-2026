# Week Angular: Introduction to Angular — First Steps

These two exams cover the absolute fundamentals of Angular. By the end you will have
created your first Angular application, understood the component model, worked with
templates and data binding, and built a small interactive UI — without needing any
prior Angular experience.

> **Pre-requisite:** Node.js (v18+) installed — verify with `node -v`.
> You do not need to know React or any other frontend framework.

---

## What is Angular?

Angular is a **component-based frontend framework** built by Google and written in
TypeScript. You build a UI by composing small, reusable **components** — each one owns
a template (HTML), styles (CSS), and logic (TypeScript). Angular's CLI tooling,
built-in dependency injection, and two-way data binding make it well-suited for large
enterprise applications.

```
┌─────────────────────────────────────────────────┐
│                  Angular App                    │
│                                                 │
│  ┌─────────────┐   ┌─────────────┐             │
│  │  Component  │   │  Component  │   ...        │
│  │ ─────────── │   │ ─────────── │             │
│  │ template    │   │ template    │             │
│  │ (HTML)      │   │ (HTML)      │             │
│  │ styles      │   │ styles      │             │
│  │ (CSS)       │   │ (CSS)       │             │
│  │ class       │   │ class       │             │
│  │ (TypeScript)│   │ (TypeScript)│             │
│  └─────────────┘   └─────────────┘             │
│                                                 │
│  ┌──────────────────────────────────────┐       │
│  │              Services                │       │
│  │  (shared logic, data, HTTP calls)    │       │
│  └──────────────────────────────────────┘       │
└─────────────────────────────────────────────────┘
```

---

# Exam 1 — Your First Angular App & Components

## Objectives

- Install the Angular CLI and generate a new project
- Understand the project structure
- Create and use components
- Work with interpolation, property binding, and event binding
- Pass data between a parent and a child component using `@Input`

---

### Task 1 — Install the CLI and create a project

Open a terminal and run:

```bash
npm install -g @angular/cli
ng new student-app --routing=false --style=css
cd student-app
ng serve
```

Open `http://localhost:4200` in a browser. You should see the Angular welcome page.

**Understanding the generated structure:**

```
student-app/
├── src/
│   ├── app/
│   │   ├── app.component.ts      ← root component (TypeScript)
│   │   ├── app.component.html    ← root component template (HTML)
│   │   ├── app.component.css     ← root component styles
│   │   └── app.module.ts         ← root module (registers all components)
│   ├── index.html                ← single HTML page — Angular renders into <app-root>
│   └── main.ts                   ← bootstraps the app
├── angular.json                  ← CLI configuration
└── package.json
```

> **Key concept — Single Page Application (SPA):** There is exactly one `index.html`.
> Angular replaces the content of `<app-root>` dynamically in the browser — no full
> page reload ever happens. Every "page" you navigate to is just Angular swapping
> components in and out of the DOM.

---

### Task 2 — Understand `app.component.ts`

Open `src/app/app.component.ts`:

```typescript
import { Component } from '@angular/core';

@Component({
  selector: 'app-root',        // the HTML tag <app-root> refers to this component
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'student-app';       // a class property
}
```

Open `src/app/app.component.html` and replace its entire content with:

```html
<h1>Welcome to {{ title }}!</h1>
```

Save the file. The browser should live-reload and show **Welcome to student-app!**

**What is `{{ title }}`?**
This is **interpolation** — Angular evaluates the expression inside `{{ }}` and inserts
the result as text. `title` refers to the `title` property on the component class.
Change `title = 'student-app'` to `title = 'My Angular Journey'` and watch the browser
update automatically.

---

### Task 3 — Create a `StudentCardComponent`

Generate a new component using the CLI:

```bash
ng generate component student-card
# shorthand: ng g c student-card
```

This creates:

```
src/app/student-card/
├── student-card.component.ts
├── student-card.component.html
└── student-card.component.css
```

It also **automatically registers** the component in `app.module.ts` — you can verify
this by opening that file and seeing `StudentCardComponent` in the `declarations` array.

Open `student-card.component.ts` and add a property:

```typescript
import { Component } from '@angular/core';

@Component({
  selector: 'app-student-card',
  templateUrl: './student-card.component.html',
  styleUrls: ['./student-card.component.css']
})
export class StudentCardComponent {
  studentName = 'Stoyan';
  grade = 'A';
  isEnrolled = true;
}
```

Open `student-card.component.html`:

```html
<div class="card">
  <h2>{{ studentName }}</h2>
  <p>Grade: {{ grade }}</p>
  <p>Status: {{ isEnrolled ? 'Enrolled' : 'Not enrolled' }}</p>
</div>
```

Add basic styles to `student-card.component.css`:

```css
.card {
  border: 1px solid #ccc;
  border-radius: 8px;
  padding: 16px;
  max-width: 240px;
  font-family: sans-serif;
}
```

Now use the component in `app.component.html`:

```html
<h1>{{ title }}</h1>
<app-student-card></app-student-card>
```

The browser should display a card with the student info.

> **Discussion point:** Why does Angular scope CSS per component? Try adding a global
> `h2 { color: red; }` rule in `styles.css` (the global stylesheet). Does it affect
> the card's `<h2>`? Then add the same rule directly in `student-card.component.css`.
> What is the difference?

---

### Task 4 — Property binding and event binding

Add a button to the card template that toggles enrolment status.

`student-card.component.html`:

```html
<div class="card">
  <h2>{{ studentName }}</h2>
  <p>Grade: {{ grade }}</p>
  <p>Status: {{ isEnrolled ? 'Enrolled' : 'Not enrolled' }}</p>

  <!-- Event binding: (click) calls the method when the button is clicked -->
  <button (click)="toggleEnrolment()">Toggle Enrolment</button>

  <!-- Property binding: [disabled] sets the HTML attribute from a TS expression -->
  <button [disabled]="!isEnrolled" (click)="submitGrade()">Submit Grade</button>
</div>
```

`student-card.component.ts`:

```typescript
export class StudentCardComponent {
  studentName = 'Stoyan';
  grade = 'A';
  isEnrolled = true;

  toggleEnrolment(): void {
    this.isEnrolled = !this.isEnrolled;
  }

  submitGrade(): void {
    alert(`Grade ${this.grade} submitted for ${this.studentName}`);
  }
}
```

Click the **Toggle Enrolment** button. The status text should change, and the
**Submit Grade** button should become disabled when the student is not enrolled.

**Binding syntax summary:**

| Syntax | Direction | Example |
|--------|-----------|---------|
| `{{ expr }}` | Component → DOM (text) | `{{ title }}` |
| `[prop]="expr"` | Component → DOM (attribute) | `[disabled]="!isEnrolled"` |
| `(event)="method()"` | DOM → Component | `(click)="toggleEnrolment()"` |
| `[(ngModel)]="prop"` | Both directions | covered in Exam 2 |

---

### Task 5 — Pass data with `@Input`

Right now the student name and grade are hardcoded inside the component. Make them
configurable from outside using `@Input`.

`student-card.component.ts`:

```typescript
import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-student-card',
  templateUrl: './student-card.component.html',
  styleUrls: ['./student-card.component.css']
})
export class StudentCardComponent {
  @Input() studentName = 'Unknown';
  @Input() grade = 'N/A';
  isEnrolled = true;

  toggleEnrolment(): void {
    this.isEnrolled = !this.isEnrolled;
  }

  submitGrade(): void {
    alert(`Grade ${this.grade} submitted for ${this.studentName}`);
  }
}
```

Now render multiple cards in `app.component.html` by passing different values:

```html
<h1>{{ title }}</h1>

<!-- Static string values — note NO square brackets needed for plain strings -->
<app-student-card studentName="Alice" grade="A"></app-student-card>

<!-- Property binding from the parent component's class -->
<app-student-card [studentName]="featuredStudent" [grade]="featuredGrade">
</app-student-card>
```

`app.component.ts`:

```typescript
export class AppComponent {
  title = 'student-app';
  featuredStudent = 'Bob';
  featuredGrade = 'B+';
}
```

> **Key rule:** `studentName="Alice"` passes the *literal string* `"Alice"`.
> `[studentName]="featuredStudent"` passes the *value* of the `featuredStudent`
> property — it evaluates the expression. If you write `[studentName]="'Alice'"` (with
> inner quotes), you are back to passing a literal string via binding syntax.
> The distinction matters as soon as you pass numbers or booleans:
> `[count]="5"` passes the number `5`, while `count="5"` passes the string `"5"`.

---

### Task 6 — Display a list with `*ngFor`

Add an array of students to `app.component.ts` and render all of them:

```typescript
export class AppComponent {
  title = 'student-app';

  students = [
    { name: 'Alice', grade: 'A' },
    { name: 'Bob',   grade: 'B+' },
    { name: 'Carol', grade: 'A-' },
  ];
}
```

`app.component.html`:

```html
<h1>{{ title }}</h1>

<app-student-card
  *ngFor="let s of students"
  [studentName]="s.name"
  [grade]="s.grade">
</app-student-card>
```

`*ngFor` is a **structural directive** — the `*` prefix signals that it will add or
remove DOM elements. For each item in `students`, Angular stamps out one
`<app-student-card>` element.

> **Discussion point:** What happens if `students` is an empty array `[]`? What if
> it is `null`? Try both and observe the result. How would you show a "No students
> found" message when the list is empty? *(Hint: look up `*ngIf`.)*

---

### Task 7 — Conditional rendering with `*ngIf`

Show a badge on the card only when the grade is `"A"` or `"A-"`.

`student-card.component.html`:

```html
<div class="card">
  <h2>
    {{ studentName }}
    <span *ngIf="grade === 'A' || grade === 'A-'" class="badge">⭐ Top Student</span>
  </h2>
  <p>Grade: {{ grade }}</p>
  <p>Status: {{ isEnrolled ? 'Enrolled' : 'Not enrolled' }}</p>
  <button (click)="toggleEnrolment()">Toggle Enrolment</button>
  <button [disabled]="!isEnrolled" (click)="submitGrade()">Submit Grade</button>
</div>
```

```css
.badge {
  background: gold;
  border-radius: 4px;
  padding: 2px 6px;
  font-size: 0.75rem;
  margin-left: 8px;
}
```

Only Alice and Carol should show the badge.

---

## Exam 1 Checkpoint ✅

By now you should be able to:

- Create an Angular project with the CLI
- Explain what a component is and what its three files do
- Use `{{ }}`, `[prop]`, and `(event)` bindings
- Create a reusable component and pass data to it with `@Input`
- Render lists with `*ngFor` and conditionally show elements with `*ngIf`

---

# Exam 2 — Services, Forms & HTTP

## Objectives

- Create a service to hold shared state and logic
- Use Angular's dependency injection to provide the service to components
- Capture user input with a template-driven form
- Make HTTP requests to a public API
- Display loading and error states

---

### Task 1 — Create a `StudentService`

A **service** is a TypeScript class decorated with `@Injectable`. It holds logic and
data that multiple components can share. Angular's **dependency injection (DI)** system
creates a single shared instance and provides it to any component that asks for it.

```bash
ng generate service student
# shorthand: ng g s student
```

This creates `src/app/student.service.ts`. Open it and replace the contents:

```typescript
import { Injectable } from '@angular/core';

export interface Student {
  name: string;
  grade: string;
}

@Injectable({
  providedIn: 'root'   // one shared instance for the whole app
})
export class StudentService {

  private students: Student[] = [
    { name: 'Alice', grade: 'A' },
    { name: 'Bob',   grade: 'B+' },
    { name: 'Carol', grade: 'A-' },
  ];

  getStudents(): Student[] {
    return this.students;
  }

  addStudent(student: Student): void {
    this.students.push(student);
  }

  removeStudent(name: string): void {
    this.students = this.students.filter(s => s.name !== name);
  }
}
```

**Why move the array to a service?** If both a student list component and a statistics
component need the same data, hardcoding it in one component forces the other to
duplicate it. A service is the single source of truth.

---

### Task 2 — Inject the service into the component

Update `app.component.ts` to use the service instead of its own array:

```typescript
import { Component, OnInit } from '@angular/core';
import { StudentService, Student } from './student.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  title = 'student-app';
  students: Student[] = [];

  // Angular injects StudentService automatically because of the type annotation
  constructor(private studentService: StudentService) {}

  ngOnInit(): void {
    this.students = this.studentService.getStudents();
  }

  removeStudent(name: string): void {
    this.studentService.removeStudent(name);
    this.students = this.studentService.getStudents();
  }
}
```

`app.component.html` — add a remove button to each card:

```html
<h1>{{ title }}</h1>

<app-student-card
  *ngFor="let s of students"
  [studentName]="s.name"
  [grade]="s.grade"
  (remove)="removeStudent(s.name)">
</app-student-card>
```

To make the `remove` button work, add an `@Output` to `StudentCardComponent`:

```typescript
import { Component, Input, Output, EventEmitter } from '@angular/core';

export class StudentCardComponent {
  @Input() studentName = 'Unknown';
  @Input() grade = 'N/A';
  @Output() remove = new EventEmitter<void>();
  isEnrolled = true;

  onRemove(): void {
    this.remove.emit();
  }
  // ... rest unchanged
}
```

```html
<!-- inside student-card.component.html -->
<button (click)="onRemove()">Remove</button>
```

> **`@Input` vs `@Output` summary:**
> - `@Input()` — parent passes data **down** to the child
> - `@Output()` + `EventEmitter` — child sends events **up** to the parent
>
> Data always flows down; events always flow up. This one-way data flow makes it easy
> to trace where a bug came from.

---

### Task 3 — Add a student with a template-driven form

Enable the `FormsModule` (needed for `ngModel`) in `app.module.ts`:

```typescript
import { FormsModule } from '@angular/forms';

@NgModule({
  declarations: [ AppComponent, StudentCardComponent ],
  imports: [ BrowserModule, FormsModule ],   // ← add FormsModule here
  bootstrap: [ AppComponent ]
})
export class AppModule {}
```

Create an `AddStudentFormComponent`:

```bash
ng g c add-student-form
```

`add-student-form.component.ts`:

```typescript
import { Component, Output, EventEmitter } from '@angular/core';
import { Student } from '../student.service';

@Component({
  selector: 'app-add-student-form',
  templateUrl: './add-student-form.component.html',
  styleUrls: ['./add-student-form.component.css']
})
export class AddStudentFormComponent {
  @Output() studentAdded = new EventEmitter<Student>();

  newName = '';
  newGrade = '';

  onSubmit(): void {
    if (!this.newName.trim() || !this.newGrade.trim()) {
      return;
    }
    this.studentAdded.emit({ name: this.newName, grade: this.newGrade });
    this.newName = '';
    this.newGrade = '';
  }
}
```

`add-student-form.component.html`:

```html
<div class="form-container">
  <h3>Add Student</h3>

  <!-- [(ngModel)] is two-way binding: changes in the input update newName,
       and changes to newName in code update the input field -->
  <input [(ngModel)]="newName" placeholder="Student name" />
  <input [(ngModel)]="newGrade" placeholder="Grade (e.g. A, B+)" />
  <button (click)="onSubmit()">Add</button>
</div>
```

Wire it up in `app.component.html`:

```html
<h1>{{ title }}</h1>

<app-add-student-form
  (studentAdded)="onStudentAdded($event)">
</app-add-student-form>

<app-student-card
  *ngFor="let s of students"
  [studentName]="s.name"
  [grade]="s.grade"
  (remove)="removeStudent(s.name)">
</app-student-card>
```

And in `app.component.ts`:

```typescript
onStudentAdded(student: Student): void {
  this.studentService.addStudent(student);
  this.students = this.studentService.getStudents();
}
```

Fill the form and click **Add**. A new card should appear immediately.

> **`[(ngModel)]` — the banana-in-a-box syntax:**
> `[ngModel]="newName"` alone would be one-way (component → input field).
> `(ngModelChange)="newName = $event"` alone would be one-way (input field → component).
> `[(ngModel)]="newName"` is shorthand for both at once — hence "two-way binding".
> The square brackets carry the value in; the parentheses carry changes out.

---

### Task 4 — Make an HTTP request

Angular includes `HttpClient` for making HTTP requests. It returns **Observables** —
Angular's approach to async data streams.

Enable `HttpClientModule` in `app.module.ts`:

```typescript
import { HttpClientModule } from '@angular/common/http';

@NgModule({
  imports: [ BrowserModule, FormsModule, HttpClientModule ],
  // ...
})
export class AppModule {}
```

Create a `UniversityService` that fetches from a public API:

```bash
ng g s university
```

`university.service.ts`:

```typescript
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface University {
  name: string;
  country: string;
  'web_pages': string[];
}

@Injectable({ providedIn: 'root' })
export class UniversityService {

  private apiUrl = 'http://universities.hipolabs.com/search';

  constructor(private http: HttpClient) {}

  searchByCountry(country: string): Observable<University[]> {
    return this.http.get<University[]>(`${this.apiUrl}?country=${country}`);
  }
}
```

Create a `UniversitySearchComponent`:

```bash
ng g c university-search
```

`university-search.component.ts`:

```typescript
import { Component } from '@angular/core';
import { UniversityService, University } from '../university.service';

@Component({
  selector: 'app-university-search',
  templateUrl: './university-search.component.html',
  styleUrls: ['./university-search.component.css']
})
export class UniversitySearchComponent {
  country = '';
  results: University[] = [];
  isLoading = false;
  errorMessage = '';

  constructor(private universityService: UniversityService) {}

  search(): void {
    if (!this.country.trim()) return;

    this.isLoading = true;
    this.errorMessage = '';
    this.results = [];

    this.universityService.searchByCountry(this.country).subscribe({
      next: (data) => {
        this.results = data;
        this.isLoading = false;
      },
      error: (err) => {
        this.errorMessage = 'Could not load data. Please try again.';
        this.isLoading = false;
        console.error(err);
      }
    });
  }
}
```

`university-search.component.html`:

```html
<div>
  <h2>University Search</h2>
  <input [(ngModel)]="country" placeholder="Enter country (e.g. Bulgaria)" />
  <button (click)="search()">Search</button>

  <p *ngIf="isLoading">Loading...</p>
  <p *ngIf="errorMessage" style="color: red;">{{ errorMessage }}</p>

  <ul *ngIf="results.length > 0">
    <li *ngFor="let uni of results">
      <strong>{{ uni.name }}</strong> —
      <a [href]="uni.web_pages[0]" target="_blank">{{ uni.web_pages[0] }}</a>
    </li>
  </ul>

  <p *ngIf="!isLoading && results.length === 0 && !errorMessage && country">
    No results found for "{{ country }}".
  </p>
</div>
```

Add `<app-university-search>` to `app.component.html` and test by searching for
`Bulgaria`, `Germany`, or any other country.

> **What is an Observable?** Think of it like a promise, but more powerful. A
> `Promise` gives you one value once. An `Observable` can emit many values over time
> and can be cancelled. `HttpClient.get()` returns an Observable that emits **one**
> response and completes — so for a simple HTTP call it behaves much like a Promise.
> You subscribe to it with `.subscribe({ next: ..., error: ..., complete: ... })`.

---

### Task 5 — Lifecycle hooks

Angular calls specific methods at defined points in a component's life. The most
important ones for beginners are:

| Hook | When it runs |
|------|-------------|
| `ngOnInit()` | Once, after the component is created and inputs are set |
| `ngOnChanges()` | Every time an `@Input` value changes |
| `ngOnDestroy()` | Just before the component is removed from the DOM |

Add logging to `StudentCardComponent` to see the hooks fire:

```typescript
import { Component, Input, OnInit, OnChanges, OnDestroy, SimpleChanges } from '@angular/core';

export class StudentCardComponent implements OnInit, OnChanges, OnDestroy {
  @Input() studentName = 'Unknown';
  @Input() grade = 'N/A';

  ngOnInit(): void {
    console.log(`[ngOnInit] Card created for: ${this.studentName}`);
  }

  ngOnChanges(changes: SimpleChanges): void {
    console.log('[ngOnChanges] Input changed:', changes);
  }

  ngOnDestroy(): void {
    console.log(`[ngOnDestroy] Card removed for: ${this.studentName}`);
  }

  // ... rest unchanged
}
```

Open the browser DevTools console and click **Remove** on one of the student cards.
You should see the `ngOnDestroy` log. Reload the page — you should see `ngOnInit`
logs for each card.

> **Why does this matter?** `ngOnInit` is the right place to fetch data or set up
> subscriptions — not the constructor. The constructor runs before Angular has
> processed `@Input` values, so `this.studentName` would still be the default there.
> `ngOnDestroy` is where you clean up subscriptions or timers to prevent memory leaks.

---

## Exam 2 Checkpoint ✅

By now you should be able to:

- Explain what a service is and why it is preferable to duplicating logic in components
- Inject a service into a component using the constructor
- Use `@Output` + `EventEmitter` to emit events from a child to a parent
- Bind form inputs with `[(ngModel)]` (two-way binding)
- Make an HTTP GET request with `HttpClient` and handle loading / error states
- Name the three most important lifecycle hooks and when they run

---

## Summary of files created across both exams

```
src/app/
├── app.component.ts              ← updated: uses StudentService, handles events
├── app.component.html            ← updated: ngFor, form, university search
├── app.module.ts                 ← updated: FormsModule, HttpClientModule added

├── student-card/
│   ├── student-card.component.ts ← @Input, @Output, lifecycle hooks
│   ├── student-card.component.html
│   └── student-card.component.css

├── add-student-form/
│   ├── add-student-form.component.ts  ← two-way binding, @Output
│   ├── add-student-form.component.html
│   └── add-student-form.component.css

├── university-search/
│   ├── university-search.component.ts  ← HttpClient, Observable, loading/error
│   ├── university-search.component.html
│   └── university-search.component.css

├── student.service.ts            ← shared state, add/remove/get students
└── university.service.ts         ← HTTP call, typed response
```

---

## Core Angular concepts covered

| Concept | Where used |
|---------|-----------|
| Component (`@Component`) | Every task |
| Template interpolation `{{ }}` | Tasks 1–2, Exam 1 |
| Property binding `[prop]` | Task 4, Exam 1 |
| Event binding `(event)` | Task 4, Exam 1 |
| `@Input` | Task 5, Exam 1 |
| `*ngFor` | Task 6, Exam 1 |
| `*ngIf` | Task 7, Exam 1 |
| `@Output` + `EventEmitter` | Task 2, Exam 2 |
| Service + dependency injection | Tasks 1–2, Exam 2 |
| Two-way binding `[(ngModel)]` | Task 3, Exam 2 |
| `HttpClient` + `Observable` | Task 4, Exam 2 |
| Lifecycle hooks | Task 5, Exam 2 |

---

## What comes next (beyond these exams)

- **Angular Router** — navigate between "pages" without reloading
- **Reactive Forms** — `FormGroup` / `FormControl` for complex validation
- **RxJS operators** — `map`, `filter`, `switchMap` to transform data streams
- **Angular Material** — pre-built UI component library
- **Standalone components** (Angular 14+) — components without `NgModule`
