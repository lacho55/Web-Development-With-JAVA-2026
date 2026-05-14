# HTML & CSS Practice Tasks

> Work through each task on your own first, then expand the **Solution** dropdown to check your answer.

---

## Task 1 — Build a Basic HTML Page Structure

Create a valid HTML5 page that includes:
- A proper `<!DOCTYPE>` declaration and `<html lang="en">`
- A `<head>` with charset, viewport meta tag, and a title of `"My Profile"`
- A `<body>` with an `<h1>` saying `"Hello, I'm [Your Name]"` and a short `<p>` bio

<details>
<summary>Solution</summary>

```html
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>My Profile</title>
  </head>
  <body>
    <h1>Hello, I'm Alex</h1>
    <p>I'm a web development student learning HTML and CSS.</p>
  </body>
</html>
```

</details>

---

## Task 2 — Links and Images

Inside a `<body>`, write the HTML for:
1. An external link to `https://developer.mozilla.org` that opens in a new tab
2. An anchor link that jumps to a section with `id="contact"` on the same page
3. An `<img>` tag with a `src`, a descriptive `alt` text, and explicit `width`/`height`

<details>
<summary>Solution</summary>

```html
<!-- 1. External link opening in a new tab -->
<a href="https://developer.mozilla.org" target="_blank" rel="noopener">MDN Web Docs</a>

<!-- 2. Anchor link to a section on the same page -->
<a href="#contact">Go to Contact</a>

<!-- 3. Image with alt text and dimensions -->
<img src="profile.jpg" alt="A smiling person sitting at a desk" width="400" height="300" />

<!-- The target section further down the page -->
<section id="contact">
  <h2>Contact</h2>
</section>
```

</details>

---

## Task 3 — Nested Lists

Create a shopping list using an **unordered list**. The `"Fruit"` item should have a nested unordered list containing `"Apple"`, `"Banana"`, and `"Mango"`. Also add `"Milk"` and `"Bread"` as top-level items.

<details>
<summary>Solution</summary>

```html
<ul>
  <li>Fruit
    <ul>
      <li>Apple</li>
      <li>Banana</li>
      <li>Mango</li>
    </ul>
  </li>
  <li>Milk</li>
  <li>Bread</li>
</ul>
```

</details>

---

## Task 4 — Data Table

Build a table showing student grades with:
- A `<caption>` of `"Student Grades"`
- A `<thead>` with columns: **Name**, **Subject**, **Grade**
- A `<tbody>` with at least two rows of data
- A `<tfoot>` row showing `"Average"` and a grade

<details>
<summary>Solution</summary>

```html
<table>
  <caption>Student Grades</caption>
  <thead>
    <tr>
      <th scope="col">Name</th>
      <th scope="col">Subject</th>
      <th scope="col">Grade</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>Alice</td>
      <td>Mathematics</td>
      <td>A</td>
    </tr>
    <tr>
      <td>Bob</td>
      <td>History</td>
      <td>B+</td>
    </tr>
  </tbody>
  <tfoot>
    <tr>
      <td colspan="2">Average</td>
      <td>A-</td>
    </tr>
  </tfoot>
</table>
```

</details>

---

## Task 5 — Registration Form

Create a form with `method="POST"` containing:
- A text input for **Full Name** (required)
- An email input for **Email** (required)
- A password input with a minimum length of 8
- A `<select>` dropdown for **Country** with at least 3 options
- A submit button labeled `"Register"`

Each input must have a properly associated `<label>`.

<details>
<summary>Solution</summary>

```html
<form action="/register" method="POST">

  <label for="fullname">Full Name:</label>
  <input type="text" id="fullname" name="fullname" placeholder="Jane Doe" required />

  <label for="email">Email:</label>
  <input type="email" id="email" name="email" placeholder="jane@example.com" required />

  <label for="password">Password:</label>
  <input type="password" id="password" name="password" minlength="8" required />

  <label for="country">Country:</label>
  <select id="country" name="country">
    <option value="">-- Select a country --</option>
    <option value="tr">Turkey</option>
    <option value="de">Germany</option>
    <option value="us">United States</option>
  </select>

  <button type="submit">Register</button>

</form>
```

</details>

---

## Task 6 — Semantic Page Layout

Using only semantic HTML5 elements, structure a page that has:
- A `<header>` with a `<nav>` containing links to Home, Blog, and Contact
- A `<main>` area with an `<article>` (with its own heading and two `<section>` tags) and an `<aside>` with related links
- A `<footer>` with a copyright notice

<details>
<summary>Solution</summary>

```html
<header>
  <nav>
    <ul>
      <li><a href="/">Home</a></li>
      <li><a href="/blog">Blog</a></li>
      <li><a href="/contact">Contact</a></li>
    </ul>
  </nav>
</header>

<main>
  <article>
    <header>
      <h1>Getting Started with HTML</h1>
    </header>
    <section>
      <h2>What is HTML?</h2>
      <p>HTML stands for HyperText Markup Language...</p>
    </section>
    <section>
      <h2>Why Learn It?</h2>
      <p>It is the foundation of every web page...</p>
    </section>
  </article>

  <aside>
    <h2>Related Articles</h2>
    <ul>
      <li><a href="#">Intro to CSS</a></li>
      <li><a href="#">JavaScript Basics</a></li>
    </ul>
  </aside>
</main>

<footer>
  <p>&copy; 2025 My Website</p>
</footer>
```

</details>

---

## Task 7 — Box Model Styling

Given a `<div class="card">` containing an `<h2>` and a `<p>`, write CSS that:
- Sets a width of `320px` and uses `box-sizing: border-box`
- Adds `24px` padding on all sides
- Adds a `1px solid #ccc` border with `8px` border-radius
- Centers the card on the page with `margin: 40px auto`
- Adds a subtle box shadow

<details>
<summary>Solution</summary>

```css
.card {
  width: 320px;
  box-sizing: border-box;
  padding: 24px;
  border: 1px solid #ccc;
  border-radius: 8px;
  margin: 40px auto;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}
```

</details>

---

## Task 8 — Flexbox Navigation Bar

Style a `<nav>` containing a logo `<span>` and a `<ul>` of links so that:
- The logo sits on the **left**, the links on the **right**
- Items are vertically centred
- The links are displayed horizontally with `16px` gap between them
- Links have no underline and turn blue on hover

<details>
<summary>Solution</summary>

```html
<nav class="navbar">
  <span class="logo">MySite</span>
  <ul class="nav-links">
    <li><a href="/">Home</a></li>
    <li><a href="/about">About</a></li>
    <li><a href="/contact">Contact</a></li>
  </ul>
</nav>
```

```css
.navbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 24px;
  background: #fff;
  border-bottom: 1px solid #e5e7eb;
  height: 60px;
}

.nav-links {
  display: flex;
  gap: 16px;
  list-style: none;
  margin: 0;
  padding: 0;
}

.nav-links a {
  text-decoration: none;
  color: #374151;
}

.nav-links a:hover {
  color: #3b82f6;
}
```

</details>

---

## Task 9 — CSS Grid Card Layout

Create a responsive card grid where:
- Cards are at least `200px` wide and stretch to fill available space
- The grid has a `16px` gap between cards
- Each `.card` has a white background, padding, border-radius, and a shadow
- On screens **768px and wider**, the grid has exactly **3 equal columns**

<details>
<summary>Solution</summary>

```css
.card-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 16px;
  padding: 24px;
}

.card {
  background: #ffffff;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

@media (min-width: 768px) {
  .card-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}
```

</details>

---

## Task 10 — CSS Variables & Button States

Define CSS custom properties on `:root` for a primary colour (`#3b82f6`), a darker variant (`#1d4ed8`), and a border-radius (`6px`). Then style a `.btn-primary` class that:
- Uses those variables for background and border-radius
- Has white text and comfortable padding
- Smoothly transitions background and `transform` on hover
- Lifts slightly (`translateY(-2px)`) on hover and returns to normal on `:active`

<details>
<summary>Solution</summary>

```css
:root {
  --color-primary: #3b82f6;
  --color-primary-dark: #1d4ed8;
  --border-radius: 6px;
}

.btn-primary {
  background: var(--color-primary);
  color: #ffffff;
  padding: 10px 20px;
  border: none;
  border-radius: var(--border-radius);
  cursor: pointer;
  font-size: 1rem;
  transition: background 0.2s ease, transform 0.15s ease;
}

.btn-primary:hover {
  background: var(--color-primary-dark);
  transform: translateY(-2px);
}

.btn-primary:active {
  transform: translateY(0);
}
```

</details>
