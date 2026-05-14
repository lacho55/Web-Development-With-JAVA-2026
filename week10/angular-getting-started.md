# Angular — Getting Started Guide

> From zero to your first working component, step by step.

---

## Prerequisites

Make sure you have the following installed before starting:

- **Node.js** (v18 or later) — [nodejs.org](https://nodejs.org)
- **npm** (comes with Node) — verify with `npm -v`
- A code editor — [VS Code](https://code.visualstudio.com) is recommended

---

## Step 1 — Install the Angular CLI

The Angular CLI is your main tool for generating and serving Angular projects.

```bash
npm install -g @angular/cli
```

Verify the installation:

```bash
ng version
```

You should see the Angular CLI version printed in the terminal.

---

## Step 2 — Create a New Project

```bash
ng new my-first-app
```

The CLI will ask you two questions:

| Prompt | Recommended choice |
|---|---|
| Which stylesheet format? | `CSS` (simplest to start) |
| Enable Server-Side Rendering (SSR)? | `No` |

Then move into the project folder:

```bash
cd my-first-app
```

---

## Step 3 — Project Structure Overview

```
my-first-app/
├── src/
│   ├── app/
│   │   ├── app.component.ts      ← root component logic
│   │   ├── app.component.html    ← root component template
│   │   ├── app.component.css     ← root component styles
│   │   └── app.config.ts         ← app-level providers (Angular 17+)
│   ├── index.html                ← single HTML shell
│   └── main.ts                   ← bootstrap entry point
├── angular.json                  ← CLI configuration
├── package.json
└── tsconfig.json
```

The most important folder is `src/app/`. Every component, service, and feature you create lives here.

---

## Step 4 — Run the Development Server

```bash
ng serve
```

Open your browser at **http://localhost:4200**. The page auto-reloads whenever you save a file.

---

## Step 5 — Understanding the Root Component

Open `src/app/app.component.ts`:

```typescript
import { Component } from '@angular/core';

@Component({
  selector: 'app-root',       // the custom HTML tag used in index.html
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'my-first-app';
}
```

Three key parts of every component:

- **`selector`** — the HTML tag that places this component on a page
- **`templateUrl`** — the HTML template file
- **`styleUrls`** — the scoped CSS file(s)

---

## Step 6 — Generate Your First Component

Use the CLI to generate a `UserCard` component:

```bash
ng generate component user-card
# shorthand:
ng g c user-card
```

This creates four files inside `src/app/user-card/`:

```
user-card/
├── user-card.component.ts
├── user-card.component.html
├── user-card.component.css
└── user-card.component.spec.ts   ← unit test file
```

---

## Step 7 — Build the UserCard Component

### 7.1 Component Logic (`user-card.component.ts`)

```typescript
import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-user-card',
  standalone: true,               // Angular 17+ uses standalone components by default
  imports: [CommonModule],
  templateUrl: './user-card.component.html',
  styleUrls: ['./user-card.component.css']
})
export class UserCardComponent {
  // @Input() lets a parent component pass data into this component
  @Input() name: string = 'Anonymous';
  @Input() role: string = 'Member';
  @Input() avatarUrl: string = 'https://i.pravatar.cc/80';

  // A simple property used in the template
  isOnline: boolean = true;

  // A method called from the template
  toggleStatus(): void {
    this.isOnline = !this.isOnline;
  }
}
```

### 7.2 Template (`user-card.component.html`)

```html
<div class="card">
  <img [src]="avatarUrl" [alt]="name + ' avatar'" class="avatar" />

  <div class="info">
    <!-- String interpolation: {{ }} -->
    <h2>{{ name }}</h2>
    <p class="role">{{ role }}</p>

    <!-- Property binding: [class] -->
    <span [class]="isOnline ? 'badge online' : 'badge offline'">
      {{ isOnline ? 'Online' : 'Offline' }}
    </span>
  </div>

  <!-- Event binding: (click) -->
  <button (click)="toggleStatus()">Toggle Status</button>
</div>
```

**Binding cheat sheet:**

| Syntax | Type | Direction |
|---|---|---|
| `{{ value }}` | Interpolation | Component → Template |
| `[src]="value"` | Property binding | Component → Template |
| `(click)="fn()"` | Event binding | Template → Component |
| `[(ngModel)]="value"` | Two-way binding | Both directions |

### 7.3 Styles (`user-card.component.css`)

```css
.card {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  width: 200px;
  padding: 24px;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  background: #ffffff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  font-family: system-ui, sans-serif;
}

.avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  object-fit: cover;
}

.info {
  text-align: center;
}

.info h2 {
  margin: 0 0 4px;
  font-size: 1.1rem;
}

.role {
  margin: 0 0 8px;
  color: #6b7280;
  font-size: 0.9rem;
}

.badge {
  display: inline-block;
  padding: 2px 10px;
  border-radius: 999px;
  font-size: 0.8rem;
  font-weight: 600;
}

.online  { background: #d1fae5; color: #065f46; }
.offline { background: #fee2e2; color: #991b1b; }

button {
  margin-top: 8px;
  padding: 6px 16px;
  border: none;
  border-radius: 6px;
  background: #3b82f6;
  color: #fff;
  cursor: pointer;
  font-size: 0.9rem;
  transition: background 0.2s ease;
}

button:hover { background: #1d4ed8; }
```

---

## Step 8 — Use the Component in `AppComponent`

### 8.1 Import it (`app.component.ts`)

```typescript
import { Component } from '@angular/core';
import { UserCardComponent } from './user-card/user-card.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [UserCardComponent],   // ← register the component here
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {}
```

### 8.2 Place it in the template (`app.component.html`)

```html
<main style="display: flex; gap: 24px; padding: 40px; flex-wrap: wrap;">

  <!-- Passing data via @Input() properties -->
  <app-user-card
    name="Alice Johnson"
    role="Frontend Developer"
    avatarUrl="https://i.pravatar.cc/80?img=1"
  />

  <app-user-card
    name="Bob Smith"
    role="Designer"
    avatarUrl="https://i.pravatar.cc/80?img=2"
  />

  <!-- Using default values (no inputs passed) -->
  <app-user-card />

</main>
```

---

## Step 9 — Displaying a List with `*ngFor`

If you have an array of users, you can render a card for each one instead of repeating the tag manually.

In `app.component.ts`:

```typescript
import { Component } from '@angular/core';
import { NgFor } from '@angular/common';
import { UserCardComponent } from './user-card/user-card.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [NgFor, UserCardComponent],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  users = [
    { name: 'Alice Johnson', role: 'Frontend Developer', avatarUrl: 'https://i.pravatar.cc/80?img=1' },
    { name: 'Bob Smith',     role: 'Designer',           avatarUrl: 'https://i.pravatar.cc/80?img=2' },
    { name: 'Carol White',   role: 'Backend Developer',  avatarUrl: 'https://i.pravatar.cc/80?img=3' },
  ];
}
```

In `app.component.html`:

```html
<main style="display: flex; gap: 24px; padding: 40px; flex-wrap: wrap;">
  <app-user-card
    *ngFor="let user of users"
    [name]="user.name"
    [role]="user.role"
    [avatarUrl]="user.avatarUrl"
  />
</main>
```

> **Note:** Square brackets `[name]="user.name"` are property binding — they evaluate the right-hand side as a JavaScript expression. Without them, `name="user.name"` would pass the literal string `"user.name"`.

---

## Step 10 — Useful CLI Commands Reference

| Command | What it does |
|---|---|
| `ng serve` | Start dev server at localhost:4200 |
| `ng build` | Build for production (output in `dist/`) |
| `ng g c name` | Generate a component |
| `ng g s name` | Generate a service |
| `ng g pipe name` | Generate a pipe |
| `ng g directive name` | Generate a directive |
| `ng test` | Run unit tests |
| `ng lint` | Run the linter |

---
