import { Component, input, output } from '@angular/core';
import { Show } from '../../models/show.model';

@Component({
  selector: 'app-show-detail',
  standalone: true,
  templateUrl: './show-detail.component.html',
  styleUrl: './show-detail.component.css',
})
export class ShowDetailComponent {

  readonly show = input<Show | undefined>(undefined);

  readonly backRequested  = output<void>();
  readonly editRequested  = output<Show>();
}
