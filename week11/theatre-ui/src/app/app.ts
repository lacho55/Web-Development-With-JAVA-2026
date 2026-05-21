import { Component, signal } from '@angular/core';
import { ShowListComponent }   from './components/show-list/show-list.component';
import { ShowDetailComponent } from './components/show-detail/show-detail.component';
import { ShowFormComponent }   from './components/show-form/show-form.component';
import { Show } from './models/show.model';

type View = 'list' | 'detail' | 'form';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [ShowListComponent, ShowDetailComponent, ShowFormComponent],
  templateUrl: './app.html',
  styleUrl: './app.css',
})
export class AppComponent {

  readonly currentView  = signal<View>('list');
  readonly selectedShow = signal<Show | undefined>(undefined);
  readonly showToEdit   = signal<Show | undefined>(undefined);

  onShowSelected(show: Show): void {
    this.selectedShow.set(show);
    this.currentView.set('detail');
  }

  onEditRequested(show: Show): void {
    this.showToEdit.set(show);
    this.currentView.set('form');
  }

  onAddRequested(): void {
    this.showToEdit.set(undefined);
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
