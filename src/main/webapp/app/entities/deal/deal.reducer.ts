import axios from 'axios';
import { ICrudDeleteAction, ICrudGetAction, ICrudGetAllAction, ICrudPutAction, loadMoreDataWhenScrolled, parseHeaderForLinks } from 'react-jhipster';

import { cleanEntity } from 'app/shared/util/entity-utils';
import { FAILURE, REQUEST, SUCCESS } from 'app/shared/reducers/action-type.util';

import { defaultValue, IDeal } from 'app/shared/model/deal.model';

export const ACTION_TYPES = {
  FETCH_DEAL_LIST: 'deal/FETCH_DEAL_LIST',
  FETCH_DEAL: 'deal/FETCH_DEAL',
  CREATE_DEAL: 'deal/CREATE_DEAL',
  UPDATE_DEAL: 'deal/UPDATE_DEAL',
  DELETE_DEAL: 'deal/DELETE_DEAL',
  RESET: 'deal/RESET'
};

const initialState = {
  loading: false,
  errorMessage: null,
  entities: [] as ReadonlyArray<IDeal>,
  entity: defaultValue,
  links: { next: 0 },
  updating: false,
  totalItems: 0,
  updateSuccess: false
};

export type DealState = Readonly<typeof initialState>;

// Reducer

export default (state: DealState = initialState, action): DealState => {
  switch (action.type) {
    case REQUEST(ACTION_TYPES.FETCH_DEAL_LIST):
    case REQUEST(ACTION_TYPES.FETCH_DEAL):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        loading: true
      };
    case REQUEST(ACTION_TYPES.CREATE_DEAL):
    case REQUEST(ACTION_TYPES.UPDATE_DEAL):
    case REQUEST(ACTION_TYPES.DELETE_DEAL):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        updating: true
      };
    case FAILURE(ACTION_TYPES.FETCH_DEAL_LIST):
    case FAILURE(ACTION_TYPES.FETCH_DEAL):
    case FAILURE(ACTION_TYPES.CREATE_DEAL):
    case FAILURE(ACTION_TYPES.UPDATE_DEAL):
    case FAILURE(ACTION_TYPES.DELETE_DEAL):
      return {
        ...state,
        loading: false,
        updating: false,
        updateSuccess: false,
        errorMessage: action.payload
      };
    case SUCCESS(ACTION_TYPES.FETCH_DEAL_LIST):
      const links = parseHeaderForLinks(action.payload.headers.link);
      return {
        ...state,
        links,
        loading: false,
        totalItems: action.payload.headers['x-total-count'],
        entities: loadMoreDataWhenScrolled(state.entities, action.payload.data, links)
      };
    case SUCCESS(ACTION_TYPES.FETCH_DEAL):
      return {
        ...state,
        loading: false,
        entity: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.CREATE_DEAL):
    case SUCCESS(ACTION_TYPES.UPDATE_DEAL):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.DELETE_DEAL):
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

const apiUrl = 'api/deals';

// Actions

export const getEntities: ICrudGetAllAction<IDeal> = (page, size, sort) => {
  const requestUrl = `${apiUrl}${sort ? `?page=${page}&size=${size}&sort=${sort}` : ''}`;
  return {
    type: ACTION_TYPES.FETCH_DEAL_LIST,
    payload: axios.get<IDeal>(`${requestUrl}${sort ? '&' : '?'}cacheBuster=${new Date().getTime()}`)
  };
};

export const getEntity: ICrudGetAction<IDeal> = id => {
  const requestUrl = `${apiUrl}/${id}`;
  return {
    type: ACTION_TYPES.FETCH_DEAL,
    payload: axios.get<IDeal>(requestUrl)
  };
};

export const createEntity: ICrudPutAction<IDeal> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.CREATE_DEAL,
    payload: axios.post(apiUrl, cleanEntity(entity))
  });
  return result;
};

export const updateEntity: ICrudPutAction<IDeal> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.UPDATE_DEAL,
    payload: axios.put(apiUrl, cleanEntity(entity))
  });
  return result;
};

export const deleteEntity: ICrudDeleteAction<IDeal> = id => async dispatch => {
  const requestUrl = `${apiUrl}/${id}`;
  const result = await dispatch({
    type: ACTION_TYPES.DELETE_DEAL,
    payload: axios.delete(requestUrl)
  });
  return result;
};

export const reset = () => ({
  type: ACTION_TYPES.RESET
});
