import { Component, input, output, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
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

  readonly editShow = input<Show | undefined>(undefined);

  readonly saved      = output<Show>();
  readonly cancelled  = output<void>();

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
