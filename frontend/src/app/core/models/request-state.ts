import { HttpErrorResponse } from '@angular/common/http';

export type RequestStatus = 'idle' | 'loading' | 'success' | 'empty' | 'error';
export type BannerTone = 'neutral' | 'success' | 'warning' | 'danger';

export interface RequestState<T> {
  status: RequestStatus;
  data: T;
  message: string | null;
}

export interface BannerMessage {
  tone: BannerTone;
  title: string;
  message: string;
}

export const idleState = <T>(data: T): RequestState<T> => ({
  status: 'idle',
  data,
  message: null,
});

export const loadingState = <T>(data: T): RequestState<T> => ({
  status: 'loading',
  data,
  message: null,
});

export const successState = <T>(data: T, message: string | null = null): RequestState<T> => ({
  status: 'success',
  data,
  message,
});

export const emptyState = <T>(data: T, message = 'No hay resultados disponibles.'): RequestState<T> => ({
  status: 'empty',
  data,
  message,
});

export const errorState = <T>(data: T, message: string): RequestState<T> => ({
  status: 'error',
  data,
  message,
});

export const collectionState = <T>(
  collection: T[],
  emptyMessage = 'No hay resultados disponibles.',
): RequestState<T[]> => (collection.length ? successState(collection) : emptyState(collection, emptyMessage));

export const extractErrorMessage = (error: unknown): string => {
  if (error instanceof HttpErrorResponse) {
    const payload = error.error;

    if (typeof payload === 'string' && payload.trim()) {
      return payload;
    }

    if (payload && typeof payload === 'object') {
      if ('message' in payload && typeof payload.message === 'string') {
        return payload.message;
      }

      if ('error' in payload && typeof payload.error === 'string') {
        return payload.error;
      }
    }

    if (error.status === 0) {
      return 'No fue posible conectar con el API Gateway.';
    }

    return error.statusText || `Error HTTP ${error.status}`;
  }

  if (error instanceof Error) {
    return error.message;
  }

  return 'Ocurrio un error inesperado.';
};
