import { IRegion } from 'app/entities/region/region.model';

export interface ICountry {
  id: string;
  countryName?: string | null;
  region?: Pick<IRegion, 'id'> | null;
}

export type NewCountry = Omit<ICountry, 'id'> & { id: null };
