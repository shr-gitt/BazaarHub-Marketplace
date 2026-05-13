export interface Address {
  id: number;
  userId: number;
  province: string;
  district: string;
  municipality: string;
  wardNo: number;
  street: string;
  postalCode: string;
  createdAt?: string;
  modifiedAt?: string;
}

export interface AddressRequest {
  province: string;
  district: string;
  municipality: string;
  wardNo: number;
  street: string;
  postalCode: string;
}

export interface AddressResponseDto{
  province: string;
  district: string;
  municipality: string;
  wardNo: number;
  street: string;
  postalCode: string;
}

export interface Municipality {
  label: string;
  value: string;
}

export interface District {
  label: string;
  value: string;
  municipalities: Municipality[];
}

export interface Province {
  label: string;
  value: string;
  districts: District[];
}

const OTHER = { label: 'Other', value: 'OTHER' };

export const NEPAL_PROVINCES: Province[] = [
  {
    label: 'Koshi',
    value: 'KOSHI',
    districts: [
      {
        label: 'Jhapa', value: 'JHAPA',
        municipalities: [
          { label: 'Mechinagar Municipality', value: 'MECHINAGAR_MUNICIPALITY' },
          { label: 'Bhadrapur Municipality', value: 'BHADRAPUR_MUNICIPALITY' },
          { label: 'Birtamod Municipality', value: 'BIRTAMOD_MUNICIPALITY' },
          { label: 'Damak Municipality', value: 'DAMAK_MUNICIPALITY' },
          { label: 'Kankai Municipality', value: 'KANKAI_MUNICIPALITY' },
          { label: 'Gauradaha Municipality', value: 'GAURADAHA_MUNICIPALITY' },
          { label: 'Shivasataxi Rural Municipality', value: 'SHIVASATAXI_RURAL_MUNICIPALITY' },
          OTHER,
        ],
      },
      {
        label: 'Morang', value: 'MORANG',
        municipalities: [
          { label: 'Biratnagar Metropolitan City', value: 'BIRATNAGAR_METROPOLITAN_CITY' },
          { label: 'Urlabari Municipality', value: 'URLABARI_MUNICIPALITY' },
          { label: 'Pathari Shanishchare Municipality', value: 'PATHARI_SHANISHCHARE_MUNICIPALITY' },
          { label: 'Rangeli Municipality', value: 'RANGELI_MUNICIPALITY' },
          { label: 'Sundarharaicha Municipality', value: 'SUNDARHARAICHA_MUNICIPALITY' },
          { label: 'Belbari Municipality', value: 'BELBARI_MUNICIPALITY' },
          { label: 'Letang Municipality', value: 'LETANG_MUNICIPALITY' },
          { label: 'Ratuwamai Rural Municipality', value: 'RATUWAMAI_RURAL_MUNICIPALITY' },
          { label: 'Dhanpalthan Rural Municipality', value: 'DHANPALTHAN_RURAL_MUNICIPALITY' },
          { label: 'Gramthan Rural Municipality', value: 'GRAMTHAN_RURAL_MUNICIPALITY' },
          { label: 'Miklajung Rural Municipality', value: 'MIKLAJUNG_RURAL_MUNICIPALITY' },
          OTHER,
        ],
      },
      {
        label: 'Sunsari', value: 'SUNSARI',
        municipalities: [
          { label: 'Dharan Sub-Metropolitan City', value: 'DHARAN_SUBMETROPOLITAN_CITY' },
          { label: 'Itahari Sub-Metropolitan City', value: 'ITAHARI_SUBMETROPOLITAN_CITY' },
          { label: 'Inaruwa Municipality', value: 'INARUWA_MUNICIPALITY' },
          { label: 'Ramdhuni Municipality', value: 'RAMDHUNI_MUNICIPALITY' },
          { label: 'Barahakshetra Municipality', value: 'BARAHAKSHETRA_MUNICIPALITY' },
          { label: 'Bhokraha Narsingh Rural Municipality', value: 'BHOKRAHA_NARSINGH_RURAL_MUNICIPALITY' },
          { label: 'Harinagara Rural Municipality', value: 'HARINAGARA_RURAL_MUNICIPALITY' },
          { label: 'Koshi Rural Municipality', value: 'KOSHI_RURAL_MUNICIPALITY' },
          { label: 'Gadhi Rural Municipality', value: 'GADHI_RURAL_MUNICIPALITY' },
          OTHER,
        ],
      },
      { label: 'Taplejung', value: 'TAPLEJUNG', municipalities: [OTHER] },
      { label: 'Panchthar', value: 'PANCHTHAR', municipalities: [OTHER] },
      { label: 'Ilam', value: 'ILAM', municipalities: [OTHER] },
      { label: 'Dhankuta', value: 'DHANKUTA', municipalities: [OTHER] },
      { label: 'Terhathum', value: 'TERHATHUM', municipalities: [OTHER] },
      { label: 'Sankhuwasabha', value: 'SANKHUWASABHA', municipalities: [OTHER] },
      { label: 'Bhojpur', value: 'BHOJPUR', municipalities: [OTHER] },
      { label: 'Solukhumbu', value: 'SOLUKHUMBU', municipalities: [OTHER] },
      { label: 'Okhaldhunga', value: 'OKHALDHUNGA', municipalities: [OTHER] },
      { label: 'Khotang', value: 'KHOTANG', municipalities: [OTHER] },
      { label: 'Udayapur', value: 'UDAYAPUR', municipalities: [OTHER] },
    ],
  },
  {
    label: 'Madhesh',
    value: 'MADHESH',
    districts: [
      {
        label: 'Bara', value: 'BARA',
        municipalities: [
          { label: 'Kalaiya Sub-Metropolitan City', value: 'KALAIYA_SUBMETROPOLITAN_CITY' },
          { label: 'Jitpur Simara Sub-Metropolitan City', value: 'JITPUR_SIMARA_SUBMETROPOLITAN_CITY' },
          { label: 'Nijgadh Municipality', value: 'NIJGADH_MUNICIPALITY' },
          { label: 'Simraungadh Municipality', value: 'SIMRAUNGADH_MUNICIPALITY' },
          { label: 'Mahagadhimai Municipality', value: 'MAHAGADHIMAI_MUNICIPALITY' },
          { label: 'Suwarna Municipality', value: 'SUWARNA_MUNICIPALITY' },
          { label: 'Parwanipur Rural Municipality', value: 'PARWANIPUR_RURAL_MUNICIPALITY' },
          { label: 'Pheta Rural Municipality', value: 'PHETA_RURAL_MUNICIPALITY' },
          { label: 'Prasauni Rural Municipality', value: 'PRASAUNI_RURAL_MUNICIPALITY' },
          { label: 'Adarsha Kotwal Rural Municipality', value: 'ADARSHA_KOTWAL_RURAL_MUNICIPALITY' },
          { label: 'Baragadhi Rural Municipality', value: 'BARAGADHI_RURAL_MUNICIPALITY' },
          { label: 'Bawanipur Rural Municipality', value: 'BAWANIPUR_RURAL_MUNICIPALITY' },
          { label: 'Bishrampur Rural Municipality', value: 'BISHRAMPUR_RURAL_MUNICIPALITY' },
          { label: 'Devtal Rural Municipality', value: 'DEVTAL_RURAL_MUNICIPALITY' },
          { label: 'Kaudena Rural Municipality', value: 'KAUDENA_RURAL_MUNICIPALITY' },
          { label: 'Koilabi Rural Municipality', value: 'KOILABI_RURAL_MUNICIPALITY' },
          OTHER,
        ],
      },
      { label: 'Parsa', value: 'PARSA',
        municipalities: [
          { label: 'Birgunj Metropolitan City', value: 'OTHER' },
          OTHER,
        ],
      },
      { label: 'Saptari', value: 'SAPTARI', municipalities: [OTHER] },
      { label: 'Siraha', value: 'SIRAHA', municipalities: [OTHER] },
      { label: 'Dhanusha', value: 'DHANUSHA', municipalities: [OTHER] },
      { label: 'Mahottari', value: 'MAHOTTARI', municipalities: [OTHER] },
      { label: 'Sarlahi', value: 'SARLAHI', municipalities: [OTHER] },
      { label: 'Rautahat', value: 'RAUTAHAT', municipalities: [OTHER] },
    ],
  },
  {
    label: 'Bagmati',
    value: 'BAGMATI',
    districts: [
      {
        label: 'Kathmandu', value: 'KATHMANDU',
        municipalities: [
          { label: 'Kathmandu Metropolitan City', value: 'KATHMANDU_METROPOLITAN_CITY' },
          { label: 'Budhanilkantha Municipality', value: 'BUDHANILKANTHA_MUNICIPALITY' },
          { label: 'Chandragiri Municipality', value: 'CHANDRAGIRI_MUNICIPALITY' },
          { label: 'Dakshinkali Municipality', value: 'DAKSHINKALI_MUNICIPALITY' },
          { label: 'Gokarneshwar Municipality', value: 'GOKARNESHWAR_MUNICIPALITY' },
          { label: 'Kageshwori Manohara Municipality', value: 'KAGESHWORI_MANOHARA_MUNICIPALITY' },
          { label: 'Kirtipur Municipality', value: 'KIRTIPUR_MUNICIPALITY' },
          { label: 'Nagarjun Municipality', value: 'NAGARJUN_MUNICIPALITY' },
          { label: 'Shankarapur Municipality', value: 'SHANKARAPUR_MUNICIPALITY' },
          { label: 'Tarakeshwar Municipality', value: 'TARAKESHWAR_MUNICIPALITY' },
          { label: 'Tokha Municipality', value: 'TOKHA_MUNICIPALITY' },
          OTHER,
        ],
      },
      {
        label: 'Lalitpur', value: 'LALITPUR',
        municipalities: [
          { label: 'Lalitpur Metropolitan City', value: 'LALITPUR_METROPOLITAN_CITY' },
          { label: 'Godawari Municipality', value: 'GODAWARI_MUNICIPALITY' },
          { label: 'Mahalaxmi Municipality', value: 'MAHALAXMI_MUNICIPALITY' },
          { label: 'Bagmati Rural Municipality', value: 'BAGMATI_RURAL_MUNICIPALITY' },
          { label: 'Konjyosom Rural Municipality', value: 'KONJYOSOM_RURAL_MUNICIPALITY' },
          OTHER,
        ],
      },
      {
        label: 'Bhaktapur', value: 'BHAKTAPUR',
        municipalities: [
          { label: 'Bhaktapur Municipality', value: 'BHAKTAPUR_MUNICIPALITY' },
          { label: 'Madhyapur Thimi Municipality', value: 'MADHYAPUR_THIMI_MUNICIPALITY' },
          { label: 'Changunarayan Municipality', value: 'OTHER' },
          { label: 'Suryabinayak Municipality', value: 'OTHER' },
          OTHER,
        ],
      },
      {
        label: 'Chitwan', value: 'CHITWAN',
        municipalities: [
          { label: 'Bharatpur Metropolitan City', value: 'BHARATPUR_METROPOLITAN_CITY' },
          { label: 'Ratnanagar Municipality', value: 'RATNANAGAR_MUNICIPALITY' },
          { label: 'Rapti Municipality', value: 'RAPTI_MUNICIPALITY' },
          { label: 'Khairahani Municipality', value: 'KHAIRAHANI_MUNICIPALITY' },
          { label: 'Kalika Municipality', value: 'KALIKA_MUNICIPALITY' },
          { label: 'Ichchhakamana Rural Municipality', value: 'ICHCHHAKAMANA_RURAL_MUNICIPALITY' },
          OTHER,
        ],
      },
      { label: 'Sindhuli', value: 'SINDHULI', municipalities: [OTHER] },
      { label: 'Ramechhap', value: 'RAMECHHAP', municipalities: [OTHER] },
      { label: 'Dolakha', value: 'DOLAKHA', municipalities: [OTHER] },
      { label: 'Sindhupalchok', value: 'SINDHUPALCHOK', municipalities: [OTHER] },
      { label: 'Kavrepalanchok', value: 'KAVREPALANCHOK', municipalities: [OTHER] },
      { label: 'Nuwakot', value: 'NUWAKOT', municipalities: [OTHER] },
      { label: 'Rasuwa', value: 'RASUWA', municipalities: [OTHER] },
      { label: 'Dhading', value: 'DHADING', municipalities: [OTHER] },
      { label: 'Makwanpur', value: 'MAKWANPUR', municipalities: [OTHER] },
    ],
  },
  {
    label: 'Gandaki',
    value: 'GANDAKI',
    districts: [
      {
        label: 'Kaski', value: 'KASKI',
        municipalities: [
          { label: 'Pokhara Metropolitan City', value: 'POKHARA_METROPOLITAN_CITY' },
          { label: 'Annapurna Rural Municipality', value: 'ANNAPURNA_RURAL_MUNICIPALITY' },
          { label: 'Machhapuchchhre Rural Municipality', value: 'MACHHAPUCHCHHRE_RURAL_MUNICIPALITY' },
          { label: 'Madi Rural Municipality', value: 'MADI_RURAL_MUNICIPALITY' },
          { label: 'Rupa Rural Municipality', value: 'RUPA_RURAL_MUNICIPALITY' },
          OTHER,
        ],
      },
      { label: 'Gorkha', value: 'GORKHA', municipalities: [OTHER] },
      { label: 'Lamjung', value: 'LAMJUNG', municipalities: [OTHER] },
      { label: 'Manang', value: 'MANANG', municipalities: [OTHER] },
      { label: 'Mustang', value: 'MUSTANG', municipalities: [OTHER] },
      { label: 'Myagdi', value: 'MYAGDI', municipalities: [OTHER] },
      { label: 'Nawalpur', value: 'NAWALPUR', municipalities: [OTHER] },
      { label: 'Syangja', value: 'SYANGJA', municipalities: [OTHER] },
      { label: 'Parbat', value: 'PARBAT', municipalities: [OTHER] },
      { label: 'Baglung', value: 'BAGLUNG', municipalities: [OTHER] },
      { label: 'Tanahun', value: 'TANAHU', municipalities: [OTHER] },
    ],
  },
  {
    label: 'Lumbini',
    value: 'LUMBINI',
    districts: [
      {
        label: 'Rupandehi', value: 'RUPANDEHI',
        municipalities: [
          { label: 'Butwal Sub-Metropolitan City', value: 'BUTWAL_SUBMETROPOLITAN_CITY' },
          { label: 'Tilottama Municipality', value: 'TILOTTAMA_MUNICIPALITY' },
          { label: 'Devdaha Municipality', value: 'DEVDAHA_MUNICIPALITY' },
          { label: 'Lumbini Sanskriti Municipality', value: 'LUMBINI_SANSKRITI_MUNICIPALITY' },
          { label: 'Marchawari Rural Municipality', value: 'MARCHAWARI_RURAL_MUNICIPALITY' },
          { label: 'Maya Devi Rural Municipality', value: 'MAYA_DEVI_RURAL_MUNICIPALITY' },
          { label: 'Omsatiya Rural Municipality', value: 'OMSATIYA_RURAL_MUNICIPALITY' },
          { label: 'Rohini Rural Municipality', value: 'ROHINI_RURAL_MUNICIPALITY' },
          { label: 'Sammarimai Rural Municipality', value: 'SAMMARIMAI_RURAL_MUNICIPALITY' },
          { label: 'Shudhodhan Rural Municipality', value: 'SHUDHODHAN_RURAL_MUNICIPALITY' },
          { label: 'Siyari Rural Municipality', value: 'SIYARI_RURAL_MUNICIPALITY' },
          OTHER,
        ],
      },
      {
        label: 'Banke', value: 'BANKE',
        municipalities: [
          { label: 'Nepalgunj Sub-Metropolitan City', value: 'NEPALGUNJ_SUBMETROPOLITAN_CITY' },
          { label: 'Kohalpur Municipality', value: 'KOHALPUR_MUNICIPALITY' },
          { label: 'Duduwa Rural Municipality', value: 'DUDUWA_RURAL_MUNICIPALITY' },
          { label: 'Janki Rural Municipality', value: 'JANKI_RURAL_MUNICIPALITY' },
          { label: 'Khajura Rural Municipality', value: 'KHAJURA_RURAL_MUNICIPALITY' },
          { label: 'Narainapur Rural Municipality', value: 'NARAINAPUR_RURAL_MUNICIPALITY' },
          { label: 'Rapti Sonari Rural Municipality', value: 'RAPTI_SONARI_RURAL_MUNICIPALITY' },
          OTHER,
        ],
      },
      { label: 'Arghakhanchi', value: 'ARGHAKHANCHI', municipalities: [OTHER] },
      { label: 'Bardiya', value: 'BARDIYA', municipalities: [OTHER] },
      { label: 'Dang', value: 'DANG', municipalities: [OTHER] },
      { label: 'Gulmi', value: 'GULMI', municipalities: [OTHER] },
      { label: 'Kapilvastu', value: 'KAPILVASTU', municipalities: [OTHER] },
      { label: 'Nawalparasi West', value: 'NAWALPARASI_WEST', municipalities: [OTHER] },
      { label: 'Palpa', value: 'PALPA', municipalities: [OTHER] },
      { label: 'Pyuthan', value: 'PYUTHAN', municipalities: [OTHER] },
      { label: 'Rolpa', value: 'ROLPA', municipalities: [OTHER] },
      { label: 'Rukum East', value: 'EASTERN_RUKUM', municipalities: [OTHER] },
    ],
  },
  {
    label: 'Karnali',
    value: 'KARNALI',
    districts: [
      { label: 'Dailekh', value: 'DAILEKH', municipalities: [OTHER] },
      { label: 'Dolpa', value: 'DOLPA', municipalities: [OTHER] },
      { label: 'Humla', value: 'HUMLA', municipalities: [OTHER] },
      { label: 'Jajarkot', value: 'JAJARKOT', municipalities: [OTHER] },
      { label: 'Jumla', value: 'JUMLA', municipalities: [OTHER] },
      { label: 'Kalikot', value: 'KALIKOT', municipalities: [OTHER] },
      { label: 'Mugu', value: 'MUGU', municipalities: [OTHER] },
      { label: 'Rukum West', value: 'WESTERN_RUKUM', municipalities: [OTHER] },
      { label: 'Salyan', value: 'SALYAN', municipalities: [OTHER] },
      { label: 'Surkhet', value: 'SURKHET', municipalities: [OTHER] },
    ],
  },
  {
    label: 'Sudurpashchim',
    value: 'SUDURPASHCHIM',
    districts: [
      {
        label: 'Kailali', value: 'KAILALI',
        municipalities: [
          { label: 'Dhangadhi Sub-Metropolitan City', value: 'DHANGADHI_SUBMETROPOLITAN_CITY' },
          { label: 'Tikapur Municipality', value: 'TIKAPUR_MUNICIPALITY' },
          { label: 'Bhajani Municipality', value: 'BHAJANI_MUNICIPALITY' },
          { label: 'Ghodaghodi Municipality', value: 'GHODAGHODI_MUNICIPALITY' },
          { label: 'Bardagoriya Rural Municipality', value: 'BARDAGORIYA_RURAL_MUNICIPALITY' },
          { label: 'Chure Rural Municipality', value: 'CHURE_RURAL_MUNICIPALITY' },
          { label: 'Janaki Rural Municipality', value: 'JANAKI_RURAL_MUNICIPALITY' },
          { label: 'Joshipur Rural Municipality', value: 'JOSHIPUR_RURAL_MUNICIPALITY' },
          { label: 'Kailari Rural Municipality', value: 'KAILARI_RURAL_MUNICIPALITY' },
          { label: 'Lamki Chuha Municipality', value: 'LAMKI_CHUHA_MUNICIPALITY' },
          { label: 'Mohanyal Rural Municipality', value: 'MOHANYAL_RURAL_MUNICIPALITY' },
          { label: 'Phulbari Municipality', value: 'PHULBARI_MUNICIPALITY' },
          OTHER,
        ],
      },
      { label: 'Achham', value: 'ACHHAM', municipalities: [OTHER] },
      { label: 'Baitadi', value: 'BAITADI', municipalities: [OTHER] },
      { label: 'Bajhang', value: 'BAJHANG', municipalities: [OTHER] },
      { label: 'Bajura', value: 'BAJURA', municipalities: [OTHER] },
      { label: 'Dadeldhura', value: 'DADELDHURA', municipalities: [OTHER] },
      { label: 'Darchula', value: 'DARCHULA', municipalities: [OTHER] },
      { label: 'Doti', value: 'DOTI', municipalities: [OTHER] },
      { label: 'Kanchanpur', value: 'KANCHANPUR', municipalities: [OTHER] },
    ],
  },
];