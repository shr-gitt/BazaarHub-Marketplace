import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-empty-state',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './empty-state.component.html',
  styleUrls: ['./empty-state.component.scss'],
})
export class EmptyStateComponent {
  @Input() icon = 'inbox';
  @Input() title = 'No data found';
  @Input() description = 'There is currently no data to display here.';
  @Input() actionLabel?: string;
  @Output() action = new EventEmitter<void>();
}
