import React from 'react';
import { Translate } from 'react-jhipster';

import { NavbarBrand } from 'reactstrap';
import { NavLink as Link } from 'react-router-dom';

import appConfig from 'app/config/constants';

export const BrandIcon = props => (
  <div {...props} className="brand-icon">
    <img src="content/images/jhipster_family_member_0.svg" alt="Logo" />
  </div>
);

export const Brand = props => (
  <NavbarBrand tag={Link} to="/" className="brand-logo">
    <BrandIcon />
    <span className="brand-title">
      <Translate contentKey="global.title">LoanExchangeBackend</Translate>
    </span>
    <span className="navbar-version">{appConfig.VERSION}</span>
  </NavbarBrand>
);
