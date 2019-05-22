import { Moment } from 'moment';
import { IUser } from 'app/shared/model/user.model';
import { IDeal } from 'app/shared/model/deal.model';

export const enum BalanceLogEvent {
  NEW_DEAL_OPEN = 'NEW_DEAL_OPEN',
  LOAN_TAKEN = 'LOAN_TAKEN',
  PERCENT_CHARGE = 'PERCENT_CHARGE',
  DEAL_PAYMENT = 'DEAL_PAYMENT',
  DEAL_CLOSED = 'DEAL_CLOSED'
}

export interface IBalanceLog {
  id?: number;
  date?: Moment;
  oldValue?: number;
  amountChanged?: number;
  type?: BalanceLogEvent;
  account?: IUser;
  deal?: IDeal;
}

export const defaultValue: Readonly<IBalanceLog> = {};
