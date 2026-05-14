import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import {UserCard} from './user-card/user-card';
import {MatButton, MatButtonModule} from '@angular/material/button';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, UserCard, MatButtonModule, MatButton],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  protected readonly title = signal('my-first-app');
  hide = false;
  elements: number[] = [];

  hideUserCard() {
    this.hide = !this.hide;
  }

  addUserCard() {
    this.elements.push(1);
  }
}
