import axios from 'axios';
import { ICrudDeleteAction, ICrudGetAction, ICrudGetAllAction, ICrudPutAction, loadMoreDataWhenScrolled, parseHeaderForLinks } from 'react-jhipster';

import { cleanEntity } from 'app/shared/util/entity-utils';
import { FAILURE, REQUEST, SUCCESS } from 'app/shared/reducers/action-type.util';

import { defaultValue, IBalanceLog } from 'app/shared/model/balance-log.model';

export const ACTION_TYPES = {
  FETCH_BALANCELOG_LIST: 'balanceLog/FETCH_BALANCELOG_LIST',
  FETCH_BALANCELOG: 'balanceLog/FETCH_BALANCELOG',
  CREATE_BALANCELOG: 'balanceLog/CREATE_BALANCELOG',
  UPDATE_BALANCELOG: 'balanceLog/UPDATE_BALANCELOG',
  DELETE_BALANCELOG: 'balanceLog/DELETE_BALANCELOG',
  RESET: 'balanceLog/RESET'
};

const initialState = {
  loading: false,
  errorMessage: null,
  entities: [] as ReadonlyArray<IBalanceLog>,
  entity: defaultValue,
  links: { next: 0 },
  updating: false,
  totalItems: 0,
  updateSuccess: false
};

export type BalanceLogState = Readonly<typeof initialState>;

// Reducer

export default (state: BalanceLogState = initialState, action): BalanceLogState => {
  switch (action.type) {
    case REQUEST(ACTION_TYPES.FETCH_BALANCELOG_LIST):
    case REQUEST(ACTION_TYPES.FETCH_BALANCELOG):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        loading: true
      };
    case REQUEST(ACTION_TYPES.CREATE_BALANCELOG):
    case REQUEST(ACTION_TYPES.UPDATE_BALANCELOG):
    case REQUEST(ACTION_TYPES.DELETE_BALANCELOG):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        updating: true
      };
    case FAILURE(ACTION_TYPES.FETCH_BALANCELOG_LIST):
    case FAILURE(ACTION_TYPES.FETCH_BALANCELOG):
    case FAILURE(ACTION_TYPES.CREATE_BALANCELOG):
    case FAILURE(ACTION_TYPES.UPDATE_BALANCELOG):
    case FAILURE(ACTION_TYPES.DELETE_BALANCELOG):
      return {
        ...state,
        loading: false,
        updating: false,
        updateSuccess: false,
        errorMessage: action.payload
      };
    case SUCCESS(ACTION_TYPES.FETCH_BALANCELOG_LIST):
      const links = parseHeaderForLinks(action.payload.headers.link);
      return {
        ...state,
        links,
        loading: false,
        totalItems: action.payload.headers['x-total-count'],
        entities: loadMoreDataWhenScrolled(state.entities, action.payload.data, links)
      };
    case SUCCESS(ACTION_TYPES.FETCH_BALANCELOG):
      return {
        ...state,
        loading: false,
        entity: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.CREATE_BALANCELOG):
    case SUCCESS(ACTION_TYPES.UPDATE_BALANCELOG):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.DELETE_BALANCELOG):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: {}
      };
    case ACTION_TYPES.RESET:
      return {
        ...initialState
      };
    default:
      return state;
  }
};

const apiUrl = 'api/balance-logs';

// Actions

export const getEntities: ICrudGetAllAction<IBalanceLog> = (page, size, sort) => {
  const requestUrl = `${apiUrl}${sort ? `?page=${page}&size=${size}&sort=${sort}` : ''}`;
  return {
    type: ACTION_TYPES.FETCH_BALANCELOG_LIST,
    payload: axios.get<IBalanceLog>(`${requestUrl}${sort ? '&' : '?'}cacheBuster=${new Date().getTime()}`)
  };
};

export const getEntity: ICrudGetAction<IBalanceLog> = id => {
  const requestUrl = `${apiUrl}/${id}`;
  return {
    type: ACTION_TYPES.FETCH_BALANCELOG,
    payload: axios.get<IBalanceLog>(requestUrl)
  };
};

export const createEntity: ICrudPutAction<IBalanceLog> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.CREATE_BALANCELOG,
    payload: axios.post(apiUrl, cleanEntity(entity))
  });
  return result;
};

export const updateEntity: ICrudPutAction<IBalanceLog> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.UPDATE_BALANCELOG,
    payload: axios.put(apiUrl, cleanEntity(entity))
  });
  return result;
};

export const deleteEntity: ICrudDeleteAction<IBalanceLog> = id => async dispatch => {
  const requestUrl = `${apiUrl}/${id}`;
  const result = await dispatch({
    type: ACTION_TYPES.DELETE_BALANCELOG,
    payload: axios.delete(requestUrl)
  });
  return result;
};

export const reset = () => ({
  type: ACTION_TYPES.RESET
});
