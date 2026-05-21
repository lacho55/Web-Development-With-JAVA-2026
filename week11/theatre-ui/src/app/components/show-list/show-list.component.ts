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

  readonly shows = this.showService.shows;

  readonly showSelected  = output<Show>();
  readonly editRequested = output<Show>();
  readonly addRequested  = output<void>();

  deleteShow(id: number): void {
    if (confirm('Are you sure you want to delete this show?')) {
      this.showService.delete(id);
    }
  }
}
