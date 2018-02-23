export const SET_OFFERS = 'SET_OFFERS';
export const ADD_OFFERS = 'ADD_OFFERS';
export const SHOW_NEW_OFFER_DIALOG = 'SHOW_NEW_OFFER_DIALOG';
export const GET_PROFILE_IMAGE = 'GET_PROFILE_IMAGE';
export const SET_PROFILE_IMAGE = 'SET_PROFILE_IMAGE';
let profileImage = null;

function setOffers(setOffers) {
  return {
    type: SET_OFFERS,
    offers: setOffers
  }
}

export function addOffers(newOffers) {
  return {
    type: ADD_OFFERS,
    offers: newOffers
  }
}

export function showNewOfferDialog() {
  return {
    type: SHOW_NEW_OFFER_DIALOG
  }
}

export function getProfileImage() {
  return {
    type: GET_PROFILE_IMAGE,
    image: profileImage
  }
}

export function setProfileImage(image) {
  profileImage = image;
  return {
    type: SET_PROFILE_IMAGE,
    profileImage: image
  }
}
