import { ChangeDetectionStrategy, Component, input } from '@angular/core';

@Component({
  selector: 'app-loading-panel',
  imports: [],
  templateUrl: './loading-panel.html',
  styleUrl: './loading-panel.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class LoadingPanel {
  readonly label = input('Cargando datos...');
}
