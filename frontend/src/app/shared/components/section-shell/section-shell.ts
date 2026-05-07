import { ChangeDetectionStrategy, Component, input } from '@angular/core';

@Component({
  selector: 'app-section-shell',
  imports: [],
  templateUrl: './section-shell.html',
  styleUrl: './section-shell.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SectionShell {
  readonly kicker = input('');
  readonly title = input.required<string>();
  readonly description = input('');
}
