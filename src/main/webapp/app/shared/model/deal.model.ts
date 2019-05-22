import { Moment } from 'moment';
import { IUser } from 'app/shared/model/user.model';

export const enum PaymentInterval {
  DAY = 'DAY',
  MONTH = 'MONTH',
  ONE_TIME = 'ONE_TIME'
}

export const enum DealStatus {
  PENDING = 'PENDING',
  ACTIVE = 'ACTIVE',
  CLOSED = 'CLOSED',
  SUCCESS = 'SUCCESS'
}

export interface IDeal {
  id?: number;
  dateOpen?: Moment;
  dateBecomeActive?: Moment;
  endDate?: Moment;
  startBalance?: number;
  percent?: number;
  successRate?: number;
  term?: number;
  paymentEvery?: PaymentInterval;
  status?: DealStatus;
  emitter?: IUser;
  recipient?: IUser;
}

export const defaultValue: Readonly<IDeal> = {};
