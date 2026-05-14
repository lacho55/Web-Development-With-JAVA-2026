# Angular — Component & Reactive Forms Task Guide

> A hands-on walkthrough. Each section introduces a concept, gives you a task to complete yourself, then reveals the full solution.

---

## What You'll Build

By the end of this guide you'll have a fully working **"Create Profile" form** component that covers:

- Reactive Forms with `FormBuilder`
- Built-in and custom validators
- Real-time validation error messages
- A multi-step form with dynamic sections
- `@Input()` / `@Output()` for parent–child communication
- Pipes and `*ngIf` / `*ngFor` in the template

---

## Prerequisites

Make sure `ReactiveFormsModule` (and optionally `CommonModule`) are imported in your module or standalone component before starting.

```bash
ng generate component profile-form
```

---

## Task 1 — Scaffold the Component with `FormBuilder`

**Goal:** Set up `ProfileFormComponent` with an injected `FormBuilder` and a `FormGroup` called `form` that has three controls: `username`, `email`, and `bio`. Don't add validators yet — just get the group wired up.

**Hint:** Inject `FormBuilder` via the constructor and call `this.fb.group({})`.

<details>
<summary>Solution</summary>

```typescript
// profile-form.component.ts
import { Component } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-profile-form',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule],
  templateUrl: './profile-form.component.html',
  styleUrls: ['./profile-form.component.css'],
})
export class ProfileFormComponent {
  form: FormGroup;

  constructor(private fb: FormBuilder) {
    this.form = this.fb.group({
      username: [''],
      email:    [''],
      bio:      [''],
    });
  }
}
```

```html
<!-- profile-form.component.html (minimal scaffold) -->
<form [formGroup]="form" (ngSubmit)="onSubmit()">
  <input formControlName="username" placeholder="Username" />
  <input type="email" formControlName="email" placeholder="Email" />
  <textarea formControlName="bio" placeholder="Bio"></textarea>
  <button type="submit">Submit</button>
</form>
```

</details>

---

## Task 2 — Add Validators

**Goal:** Extend the three controls with validation rules:

- `username` — required, minimum 3 characters, maximum 20 characters
- `email` — required, must be a valid email format
- `bio` — optional, maximum 200 characters

Also add a convenience getter for each control so you can reference them cleanly in the template (e.g. `get username() { return this.form.get('username'); }`).

<details>
<summary>Solution</summary>

```typescript
import { Component } from '@angular/core';
import {
  FormBuilder, FormGroup, Validators,
  AbstractControl, ReactiveFormsModule
} from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-profile-form',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule],
  templateUrl: './profile-form.component.html',
  styleUrls: ['./profile-form.component.css'],
})
export class ProfileFormComponent {
  form: FormGroup;

  constructor(private fb: FormBuilder) {
    this.form = this.fb.group({
      username: ['', [
        Validators.required,
        Validators.minLength(3),
        Validators.maxLength(20),
      ]],
      email: ['', [
        Validators.required,
        Validators.email,
      ]],
      bio: ['', [
        Validators.maxLength(200),
      ]],
    });
  }

  // Convenience getters
  get username(): AbstractControl { return this.form.get('username')!; }
  get email():    AbstractControl { return this.form.get('email')!; }
  get bio():      AbstractControl { return this.form.get('bio')!; }

  onSubmit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched(); // triggers all error messages at once
      return;
    }
    console.log('Form submitted:', this.form.value);
  }
}
```

</details>

---

## Task 3 — Show Validation Error Messages in the Template

**Goal:** Wire up the template so that:

- Each field turns red (add class `is-invalid`) when it is both **invalid** and **touched**
- Below each field, show the appropriate error message with `*ngIf` (one message per error type)
- The submit button is **disabled** while the form is submitting

Use these error keys: `required`, `minlength`, `maxlength`, `email`.

<details>
<summary>Solution</summary>

```html
<!-- profile-form.component.html -->
<form [formGroup]="form" (ngSubmit)="onSubmit()" novalidate>

  <!-- Username -->
  <div class="form-group">
    <label for="username">Username</label>
    <input
      id="username"
      formControlName="username"
      class="form-control"
      [class.is-invalid]="username.invalid && username.touched"
      placeholder="e.g. john_doe"
    />
    <div *ngIf="username.invalid && username.touched" class="form-error">
      <span *ngIf="username.errors?.['required']">Username is required.</span>
      <span *ngIf="username.errors?.['minlength']">At least 3 characters.</span>
      <span *ngIf="username.errors?.['maxlength']">20 characters maximum.</span>
    </div>
  </div>

  <!-- Email -->
  <div class="form-group">
    <label for="email">Email</label>
    <input
      id="email"
      type="email"
      formControlName="email"
      class="form-control"
      [class.is-invalid]="email.invalid && email.touched"
      placeholder="you@example.com"
    />
    <div *ngIf="email.invalid && email.touched" class="form-error">
      <span *ngIf="email.errors?.['required']">Email is required.</span>
      <span *ngIf="email.errors?.['email']">Enter a valid email address.</span>
    </div>
  </div>

  <!-- Bio -->
  <div class="form-group">
    <label for="bio">Bio <span class="hint">(optional, max 200 chars)</span></label>
    <textarea
      id="bio"
      formControlName="bio"
      class="form-control"
      rows="4"
      placeholder="Tell us about yourself..."
      [class.is-invalid]="bio.invalid && bio.touched"
    ></textarea>
    <div *ngIf="bio.invalid && bio.touched" class="form-error">
      <span *ngIf="bio.errors?.['maxlength']">Maximum 200 characters.</span>
    </div>
    <!-- Live character counter -->
    <small class="char-count">{{ bio.value?.length ?? 0 }} / 200</small>
  </div>

  <button type="submit" [disabled]="isSubmitting" class="btn-primary">
    {{ isSubmitting ? 'Saving...' : 'Save Profile' }}
  </button>

</form>
```

> **Tip:** `username.errors?.['required']` uses optional chaining — it returns `undefined` (falsy) instead of throwing when `errors` is `null`.

</details>

---

## Task 4 — Write a Custom Validator

**Goal:** Create a custom validator function called `noSpaces` that rejects any value containing a space character. Apply it to the `username` control and show an error message `"Username cannot contain spaces."` in the template.

A custom validator is just a function with the signature:
```typescript
(control: AbstractControl): ValidationErrors | null
```
It returns `null` if valid, or an object like `{ noSpaces: true }` if invalid.

<details>
<summary>Solution</summary>

```typescript
// Add this function above (or outside) the component class
import { AbstractControl, ValidationErrors } from '@angular/forms';

export function noSpaces(control: AbstractControl): ValidationErrors | null {
  const hasSpace = (control.value as string)?.includes(' ');
  return hasSpace ? { noSpaces: true } : null;
}
```

Then add it to the validators array in the `FormGroup`:

```typescript
username: ['', [
  Validators.required,
  Validators.minLength(3),
  Validators.maxLength(20),
  noSpaces,                   // ← custom validator added here
]],
```

And in the template, add below the existing username errors:

```html
<span *ngIf="username.errors?.['noSpaces']">Username cannot contain spaces.</span>
```

</details>

---

## Task 5 — Add a Nested Group: Social Links

**Goal:** Add a nested `FormGroup` called `social` inside the main form, containing two optional controls: `twitter` and `github`. In the template, wrap the two inputs in a `<div formGroupName="social">` and show a live preview below (`@handle` style) only when the field has a value.

<details>
<summary>Solution</summary>

```typescript
// In the constructor
this.form = this.fb.group({
  username: [ ... ],
  email:    [ ... ],
  bio:      [ ... ],
  social: this.fb.group({        // ← nested group
    twitter: [''],
    github:  [''],
  }),
});

// Getter
get social(): FormGroup { return this.form.get('social') as FormGroup; }
```

```html
<!-- In the template -->
<fieldset formGroupName="social">
  <legend>Social Links <span class="hint">(optional)</span></legend>

  <div class="form-group">
    <label for="twitter">Twitter handle</label>
    <input id="twitter" formControlName="twitter" class="form-control" placeholder="yourhandle" />
    <small *ngIf="social.get('twitter')?.value" class="preview">
      Preview: @{{ social.get('twitter')?.value }}
    </small>
  </div>

  <div class="form-group">
    <label for="github">GitHub username</label>
    <input id="github" formControlName="github" class="form-control" placeholder="yourusername" />
    <small *ngIf="social.get('github')?.value" class="preview">
      Preview: github.com/{{ social.get('github')?.value }}
    </small>
  </div>
</fieldset>
```

</details>

---

## Task 6 — Dynamic Skills List with `FormArray`

**Goal:** Add a `FormArray` called `skills` to the form. Provide:
- An `addSkill()` method that pushes a new empty `FormControl` (required, min 2 chars)
- A `removeSkill(index)` method that removes a control at the given index
- In the template, loop over the array with `*ngFor`, render an input for each, and show an **"Add Skill"** button and a **"✕"** button per row
- Show a validation error if a skill field is left empty or too short

<details>
<summary>Solution</summary>

```typescript
import { FormBuilder, FormGroup, FormArray, Validators, AbstractControl } from '@angular/forms';

// Inside the constructor
this.form = this.fb.group({
  // ...existing controls...
  skills: this.fb.array([]),   // starts empty
});

// Getters and helpers
get skillsArray(): FormArray {
  return this.form.get('skills') as FormArray;
}

addSkill(): void {
  this.skillsArray.push(
    this.fb.control('', [Validators.required, Validators.minLength(2)])
  );
}

removeSkill(index: number): void {
  this.skillsArray.removeAt(index);
}
```

```html
<!-- Skills section in the template -->
<div class="form-group">
  <label>Skills</label>

  <div
    *ngFor="let skill of skillsArray.controls; let i = index"
    class="skill-row"
  >
    <input
      [formControl]="skill"
      class="form-control"
      [class.is-invalid]="skill.invalid && skill.touched"
      placeholder="e.g. TypeScript"
    />
    <button type="button" class="btn-remove" (click)="removeSkill(i)">✕</button>

    <div *ngIf="skill.invalid && skill.touched" class="form-error">
      <span *ngIf="skill.errors?.['required']">Skill cannot be empty.</span>
      <span *ngIf="skill.errors?.['minlength']">At least 2 characters.</span>
    </div>
  </div>

  <button type="button" class="btn-secondary" (click)="addSkill()">+ Add Skill</button>
</div>
```

> **Note:** `[formControl]="skill"` (property binding) is used here instead of `formControlName` because we're working inside an `*ngFor` loop where each control is a direct reference, not a named key.

</details>

---

## Task 7 — A Confirmation Summary with `@Output()`

**Goal:** When the form is submitted successfully, instead of just logging to the console, emit the form value to the **parent component** via an `@Output()` called `profileSaved`. In the parent (`AppComponent`), listen for this event and display the submitted profile data in a read-only summary card below the form.

<details>
<summary>Solution</summary>

**Child — `profile-form.component.ts`:**

```typescript
import { Component, Output, EventEmitter } from '@angular/core';

export interface ProfileData {
  username: string;
  email: string;
  bio: string;
  social: { twitter: string; github: string };
  skills: string[];
}

// Inside the class
@Output() profileSaved = new EventEmitter<ProfileData>();

onSubmit(): void {
  if (this.form.invalid) {
    this.form.markAllAsTouched();
    return;
  }
  this.isSubmitting = true;

  // Simulate an async save (e.g. HTTP call)
  setTimeout(() => {
    this.profileSaved.emit(this.form.value as ProfileData);
    this.isSubmitting = false;
  }, 800);
}
```

**Parent — `app.component.ts`:**

```typescript
import { Component } from '@angular/core';
import { ProfileFormComponent, ProfileData } from './profile-form/profile-form.component';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [ProfileFormComponent, CommonModule],
  templateUrl: './app.component.html',
})
export class AppComponent {
  savedProfile: ProfileData | null = null;

  onProfileSaved(profile: ProfileData): void {
    this.savedProfile = profile;
  }
}
```

**Parent — `app.component.html`:**

```html
<app-profile-form (profileSaved)="onProfileSaved($event)" />

<!-- Summary card shown after save -->
<div *ngIf="savedProfile" class="summary-card">
  <h2>✅ Profile Saved!</h2>
  <p><strong>Username:</strong> {{ savedProfile.username }}</p>
  <p><strong>Email:</strong> {{ savedProfile.email }}</p>
  <p><strong>Bio:</strong> {{ savedProfile.bio || '—' }}</p>
  <p>
    <strong>Twitter:</strong>
    {{ savedProfile.social.twitter ? '@' + savedProfile.social.twitter : '—' }}
  </p>
  <p>
    <strong>GitHub:</strong>
    {{ savedProfile.social.github || '—' }}
  </p>
  <div *ngIf="savedProfile.skills.length > 0">
    <strong>Skills:</strong>
    <span
      *ngFor="let skill of savedProfile.skills"
      class="skill-badge"
    >{{ skill }}</span>
  </div>
</div>
```

</details>

---

## Task 8 — Style the Form

**Goal:** Write the CSS for the whole component. Targets:

- `.form-group` — bottom margin of `20px`
- `.form-control` — full-width, comfortable padding, border with a blue focus ring (no default outline)
- `.is-invalid` — red border
- `.form-error` — small red error text
- `.btn-primary` — blue background, white text, hover darkens it
- `.btn-secondary` — outlined style (border only, no fill)
- `.btn-remove` — small red ghost button
- `.skill-row` — flex row with a gap between input and remove button
- `.skill-badge` — pill-shaped tag with a light blue background
- `.summary-card` — white card with padding, border-radius, shadow
- `.char-count` — muted small text aligned right
- `.preview` — muted italic helper text

<details>
<summary>Solution</summary>

```css
/* profile-form.component.css */

:host {
  display: block;
  max-width: 560px;
  margin: 40px auto;
  font-family: system-ui, sans-serif;
  color: #1f2937;
}

form {
  background: #ffffff;
  padding: 32px;
  border-radius: 12px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
}

h2 { margin: 0 0 24px; font-size: 1.4rem; }

.form-group {
  margin-bottom: 20px;
}

label {
  display: block;
  font-size: 0.875rem;
  font-weight: 600;
  color: #374151;
  margin-bottom: 6px;
}

.hint {
  font-weight: 400;
  color: #9ca3af;
  font-size: 0.8rem;
}

.form-control {
  width: 100%;
  padding: 10px 14px;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  font-size: 1rem;
  box-sizing: border-box;
  transition: border-color 0.2s, box-shadow 0.2s;
}

.form-control:focus {
  outline: none;
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.15);
}

.form-control.is-invalid {
  border-color: #ef4444;
}

.form-error {
  color: #ef4444;
  font-size: 0.78rem;
  margin-top: 4px;
}

.char-count {
  display: block;
  text-align: right;
  font-size: 0.78rem;
  color: #9ca3af;
  margin-top: 4px;
}

.preview {
  display: block;
  font-size: 0.8rem;
  color: #6b7280;
  font-style: italic;
  margin-top: 4px;
}

fieldset {
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  padding: 16px 20px;
  margin-bottom: 20px;
}

legend {
  font-weight: 600;
  font-size: 0.9rem;
  padding: 0 8px;
  color: #374151;
}

/* Buttons */
.btn-primary {
  display: inline-block;
  padding: 10px 24px;
  background: #3b82f6;
  color: #fff;
  border: none;
  border-radius: 6px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: background 0.2s ease, transform 0.15s ease;
}
.btn-primary:hover:not(:disabled) {
  background: #1d4ed8;
  transform: translateY(-1px);
}
.btn-primary:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-secondary {
  display: inline-block;
  margin-top: 8px;
  padding: 8px 16px;
  background: transparent;
  color: #3b82f6;
  border: 1.5px solid #3b82f6;
  border-radius: 6px;
  font-size: 0.9rem;
  font-weight: 600;
  cursor: pointer;
  transition: background 0.2s;
}
.btn-secondary:hover {
  background: #eff6ff;
}

.btn-remove {
  padding: 6px 10px;
  background: transparent;
  color: #ef4444;
  border: 1.5px solid #ef4444;
  border-radius: 6px;
  font-size: 0.85rem;
  cursor: pointer;
  flex-shrink: 0;
  transition: background 0.2s;
}
.btn-remove:hover {
  background: #fef2f2;
}

/* Skills */
.skill-row {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  margin-bottom: 8px;
}

.skill-row .form-control {
  flex: 1;
}

/* Summary card */
.summary-card {
  margin-top: 32px;
  background: #f0fdf4;
  border: 1px solid #bbf7d0;
  border-radius: 12px;
  padding: 24px;
  font-size: 0.95rem;
  line-height: 1.7;
}

.summary-card h2 {
  color: #065f46;
  margin-bottom: 16px;
}

.skill-badge {
  display: inline-block;
  margin: 4px 4px 0 0;
  padding: 3px 12px;
  background: #dbeafe;
  color: #1e40af;
  border-radius: 999px;
  font-size: 0.82rem;
  font-weight: 600;
}
```

</details>

---

## Full Picture — How Everything Connects

```
AppComponent
│
│  (profileSaved)="onProfileSaved($event)"   ← listens for @Output
│
└── ProfileFormComponent
      │
      ├── FormGroup: form
      │     ├── FormControl: username  (required, minLen 3, maxLen 20, noSpaces)
      │     ├── FormControl: email     (required, email)
      │     ├── FormControl: bio       (maxLen 200)
      │     ├── FormGroup:  social
      │     │     ├── FormControl: twitter
      │     │     └── FormControl: github
      │     └── FormArray:  skills    (each: required, minLen 2)
      │
      └── @Output() profileSaved → emits ProfileData on valid submit
```

---

## Quick Reference — Reactive Forms Cheatsheet

| Task | Code |
|---|---|
| Create a group | `this.fb.group({ field: ['defaultValue', validators] })` |
| Create an array | `this.fb.array([])` |
| Add to array | `arr.push(this.fb.control('', validators))` |
| Remove from array | `arr.removeAt(index)` |
| Get a control | `this.form.get('fieldName')` |
| Get errors | `control.errors?.['errorKey']` |
| Mark all touched | `this.form.markAllAsTouched()` |
| Disable a control | `control.disable()` |
| Patch values | `this.form.patchValue({ field: value })` |
| Reset the form | `this.form.reset()` |
| Check validity | `this.form.valid` / `this.form.invalid` |
| Custom validator | Function returning `null` or `{ errorKey: true }` |
