import { Moment } from 'moment';
import { IUser } from 'app/shared/model/user.model';
import { IDeal } from 'app/shared/model/deal.model';

export interface IBalanceLog {
  id?: number;
  date?: Moment;
  oldValue?: number;
  amountChanged?: number;
  type?: string;
  account?: IUser;
  deal?: IDeal;
}

export const defaultValue: Readonly<IBalanceLog> = {};
