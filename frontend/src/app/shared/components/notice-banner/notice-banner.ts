import { NgClass } from '@angular/common';
import { ChangeDetectionStrategy, Component, input } from '@angular/core';

import { BannerTone } from '../../../core/models/request-state';

@Component({
  selector: 'app-notice-banner',
  imports: [NgClass],
  templateUrl: './notice-banner.html',
  styleUrl: './notice-banner.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NoticeBanner {
  readonly tone = input<BannerTone>('neutral');
  readonly title = input('');
  readonly message = input('');
}
