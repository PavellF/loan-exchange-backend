import { Moment } from 'moment';
import { IUser } from 'app/shared/model/user.model';

export const enum Period {
  DAY = 'DAY',
  MONTH = 'MONTH',
  YEAR = 'YEAR',
  ALL_TIME = 'ALL_TIME'
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
  startBalance?: number;
  percent?: number;
  fine?: number;
  successRate?: number;
  term?: number;
  paymentEvery?: Period;
  status?: DealStatus;
  autoPaymentEnabled?: boolean;
  capitalization?: boolean;
  earlyPayment?: boolean;
  emitter?: IUser;
  recipient?: IUser;
}

export const defaultValue: Readonly<IDeal> = {
  autoPaymentEnabled: false,
  capitalization: false,
  earlyPayment: false
};
