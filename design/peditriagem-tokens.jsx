/* PediTriagem — design tokens & primitives */

const PT = {
  // Brand
  primary: '#2F80ED',
  primaryDark: '#1E66C9',
  secondary: '#56CCF2',
  navy: '#1E3A5F',

  // Surfaces
  bg: '#F4F7FB',
  card: '#FFFFFF',
  hairline: 'rgba(30,58,95,0.08)',
  divider: 'rgba(30,58,95,0.06)',

  // Type
  text: '#0F2440',
  textMuted: '#5B6B82',
  textSubtle: '#8C9AAE',

  // Risk
  lowSolid: '#27AE60',
  lowSoft: '#DFF6EA',
  lowSofter: '#EFFAF3',
  modSolid: '#E2A52E',
  modSoft: '#FFF4CC',
  modSofter: '#FFFBE8',
  highSolid: '#EB5757',
  highSoft: '#FDE2E2',
  highSofter: '#FEF1F1',

  // Misc
  shadow: '0 12px 28px rgba(15, 36, 64, 0.08), 0 2px 4px rgba(15,36,64,0.04)',
  shadowLg: '0 20px 48px rgba(15, 36, 64, 0.14)',
  font: '"Nunito", -apple-system, system-ui, sans-serif',
};

// ─────────────────────────────────────────────────────────────
// Tiny SVG icon set — flat, rounded strokes, no clipart
// ─────────────────────────────────────────────────────────────
const Stroke = ({ d, size = 24, color = 'currentColor', sw = 1.8, fill = 'none', children }) => (
  <svg width={size} height={size} viewBox="0 0 24 24" fill={fill} stroke={color}
    strokeWidth={sw} strokeLinecap="round" strokeLinejoin="round" aria-hidden="true">
    {d ? <path d={d} /> : children}
  </svg>
);

const Icon = {
  // navigation
  home: (p) => <Stroke {...p}><path d="M4 11l8-7 8 7" /><path d="M6 10v9a1 1 0 0 0 1 1h3v-6h4v6h3a1 1 0 0 0 1-1v-9" /></Stroke>,
  stetho: (p) => <Stroke {...p}><path d="M6 4v6a4 4 0 0 0 8 0V4" /><path d="M6 4h2M12 4h2" /><circle cx="18" cy="14" r="2" /><path d="M10 14v2a6 6 0 0 0 6 0" /></Stroke>,
  book: (p) => <Stroke {...p}><path d="M4 5a2 2 0 0 1 2-2h12v16H6a2 2 0 0 0-2 2V5z" /><path d="M8 7h7M8 10h7" /></Stroke>,
  history: (p) => <Stroke {...p}><path d="M3 12a9 9 0 1 0 3-6.7" /><path d="M3 4v5h5" /><path d="M12 7v5l3 2" /></Stroke>,
  user: (p) => <Stroke {...p}><circle cx="12" cy="8" r="4" /><path d="M4 20c1.5-4 5-6 8-6s6.5 2 8 6" /></Stroke>,
  // ui
  back: (p) => <Stroke {...p}><path d="M15 5l-7 7 7 7" /></Stroke>,
  chevR: (p) => <Stroke {...p}><path d="M9 5l7 7-7 7" /></Stroke>,
  chevD: (p) => <Stroke {...p}><path d="M5 9l7 7 7-7" /></Stroke>,
  close: (p) => <Stroke {...p}><path d="M6 6l12 12M18 6L6 18" /></Stroke>,
  plus: (p) => <Stroke {...p}><path d="M12 5v14M5 12h14" /></Stroke>,
  check: (p) => <Stroke {...p}><path d="M5 12.5l4.5 4.5L19 7" /></Stroke>,
  bell: (p) => <Stroke {...p}><path d="M6 16V11a6 6 0 1 1 12 0v5l1.5 2H4.5L6 16z" /><path d="M10 20a2 2 0 0 0 4 0" /></Stroke>,
  search: (p) => <Stroke {...p}><circle cx="11" cy="11" r="6" /><path d="M16 16l4 4" /></Stroke>,
  shield: (p) => <Stroke {...p}><path d="M12 3l8 3v6c0 5-3.5 8-8 9-4.5-1-8-4-8-9V6l8-3z" /></Stroke>,
  info: (p) => <Stroke {...p}><circle cx="12" cy="12" r="9" /><path d="M12 11v5M12 8h.01" /></Stroke>,
  warn: (p) => <Stroke {...p}><path d="M12 4l9 16H3L12 4z" /><path d="M12 11v4M12 18h.01" /></Stroke>,
  alert: (p) => <Stroke {...p}><circle cx="12" cy="12" r="9" /><path d="M12 7v6M12 16h.01" /></Stroke>,
  phone: (p) => <Stroke {...p}><path d="M5 4h3l2 5-2.5 1.5a11 11 0 0 0 6 6L15 14l5 2v3a2 2 0 0 1-2 2A15 15 0 0 1 3 6a2 2 0 0 1 2-2z" /></Stroke>,
  heart: (p) => <Stroke {...p}><path d="M12 20s-7-4.5-7-10a4 4 0 0 1 7-2.6A4 4 0 0 1 19 10c0 5.5-7 10-7 10z" /></Stroke>,
  drop: (p) => <Stroke {...p}><path d="M12 3s6 7 6 11a6 6 0 1 1-12 0c0-4 6-11 6-11z" /></Stroke>,
  bed: (p) => <Stroke {...p}><path d="M3 18v-7h12a4 4 0 0 1 4 4v3" /><path d="M3 18h18" /><circle cx="7" cy="13" r="1.5" /></Stroke>,
  pill: (p) => <Stroke {...p}><rect x="3" y="9" width="18" height="6" rx="3" /><path d="M12 9v6" /></Stroke>,
  thermo: (p) => <Stroke {...p}><path d="M14 14V5a2 2 0 1 0-4 0v9a4 4 0 1 0 4 0z" /></Stroke>,
  // symptoms
  cough: (p) => <Stroke {...p}><path d="M4 14a6 6 0 0 1 11-3" /><path d="M14 16h6M14 19h4M14 13h5" /></Stroke>,
  vomit: (p) => <Stroke {...p}><circle cx="12" cy="9" r="5" /><path d="M9 13l-1 4M12 14v4M15 13l1 4" /></Stroke>,
  belly: (p) => <Stroke {...p}><circle cx="12" cy="12" r="8" /><path d="M12 7v10M9 10s1 1.5 3 1.5 3-1.5 3-1.5" /></Stroke>,
  lung: (p) => <Stroke {...p}><path d="M12 4v9" /><path d="M12 8c-2-3-7-2-7 2 0 4 2 8 5 8 1.5 0 2-1 2-2" /><path d="M12 8c2-3 7-2 7 2 0 4-2 8-5 8-1.5 0-2-1-2-2" /></Stroke>,
  rash: (p) => <Stroke {...p}><circle cx="8" cy="9" r="1.2" /><circle cx="14" cy="7" r="1.2" /><circle cx="17" cy="13" r="1.2" /><circle cx="10" cy="15" r="1.2" /><circle cx="15" cy="17" r="1.2" /></Stroke>,
  bandage: (p) => <Stroke {...p}><rect x="3" y="9" width="18" height="6" rx="3" transform="rotate(-25 12 12)" /><path d="M10 11h.01M12 12h.01M14 13h.01" /></Stroke>,
  ear: (p) => <Stroke {...p}><path d="M8 20c-2 0-3-2-3-4 0-2 1-3 1-5a6 6 0 1 1 12 0c0 3-3 3-4 5s0 4-3 4-2-2-3-2" /></Stroke>,
};

// ─────────────────────────────────────────────────────────────
// Primitives
// ─────────────────────────────────────────────────────────────
function Card({ children, style, onClick, padding = 16 }) {
  return (
    <div onClick={onClick} style={{
      background: PT.card, borderRadius: 20, padding,
      boxShadow: PT.shadow, border: `1px solid ${PT.hairline}`,
      cursor: onClick ? 'pointer' : 'default',
      ...style,
    }}>{children}</div>
  );
}

function Pill({ tone = 'primary', children, style }) {
  const tones = {
    primary: { bg: '#E8F1FE', fg: PT.primaryDark },
    low: { bg: PT.lowSoft, fg: '#1E7E47' },
    mod: { bg: PT.modSoft, fg: '#8A6A12' },
    high: { bg: PT.highSoft, fg: '#A33333' },
    neutral: { bg: '#EEF2F7', fg: PT.navy },
  };
  const t = tones[tone] || tones.neutral;
  return (
    <span style={{
      display: 'inline-flex', alignItems: 'center', gap: 6,
      padding: '4px 10px', borderRadius: 999, fontSize: 11, fontWeight: 700,
      background: t.bg, color: t.fg, letterSpacing: 0.2,
      ...style,
    }}>{children}</span>
  );
}

function PrimaryButton({ children, onClick, style, tone = 'primary', icon, full = true }) {
  const bgs = {
    primary: PT.primary, navy: PT.navy, low: PT.lowSolid, mod: PT.modSolid, high: PT.highSolid,
  };
  return (
    <button onClick={onClick} style={{
      width: full ? '100%' : 'auto', padding: '16px 20px', borderRadius: 16,
      background: bgs[tone], color: '#fff', border: 'none', cursor: 'pointer',
      fontSize: 16, fontWeight: 800, fontFamily: PT.font,
      display: 'inline-flex', alignItems: 'center', justifyContent: 'center', gap: 8,
      boxShadow: `0 8px 18px ${bgs[tone]}40`,
      ...style,
    }}>
      {icon}{children}
    </button>
  );
}

function GhostButton({ children, onClick, style, full = true, icon }) {
  return (
    <button onClick={onClick} style={{
      width: full ? '100%' : 'auto', padding: '14px 18px', borderRadius: 14,
      background: '#fff', color: PT.navy, border: `1.5px solid ${PT.hairline}`,
      cursor: 'pointer', fontSize: 15, fontWeight: 700, fontFamily: PT.font,
      display: 'inline-flex', alignItems: 'center', justifyContent: 'center', gap: 8,
      ...style,
    }}>{icon}{children}</button>
  );
}

function ScreenHeader({ title, onBack, right, subtitle }) {
  return (
    <div style={{ padding: '8px 20px 12px', display: 'flex', alignItems: 'center', gap: 8 }}>
      {onBack && (
        <button onClick={onBack} style={{
          width: 38, height: 38, borderRadius: 12, border: 'none',
          background: '#fff', boxShadow: PT.shadow, cursor: 'pointer',
          display: 'flex', alignItems: 'center', justifyContent: 'center', color: PT.navy,
        }}><Icon.back size={18} /></button>
      )}
      <div style={{ flex: 1, textAlign: 'center' }}>
        <div style={{ fontSize: 15, fontWeight: 800, color: PT.text }}>{title}</div>
        {subtitle && <div style={{ fontSize: 11, color: PT.textMuted, marginTop: 2 }}>{subtitle}</div>}
      </div>
      <div style={{ minWidth: 38, display: 'flex', justifyContent: 'flex-end' }}>{right}</div>
    </div>
  );
}

function ProgressBar({ value, total, tone = PT.primary }) {
  const pct = Math.max(0, Math.min(1, value / total)) * 100;
  return (
    <div style={{ padding: '6px 20px 14px' }}>
      <div style={{
        height: 6, background: '#E6ECF3', borderRadius: 999, overflow: 'hidden',
      }}>
        <div style={{
          width: `${pct}%`, height: '100%', background: tone, borderRadius: 999,
          transition: 'width .35s cubic-bezier(.2,.8,.2,1)',
        }} />
      </div>
      <div style={{
        marginTop: 8, fontSize: 11, color: PT.textMuted, display: 'flex',
        justifyContent: 'space-between', fontWeight: 700,
      }}>
        <span>Etapa {value} de {total}</span>
        <span>{Math.round(pct)}%</span>
      </div>
    </div>
  );
}

// ─────────────────────────────────────────────────────────────
// Mascot — gentle abstract "care" mark (a soft sun/face-ish dot)
// Not a literal child illustration — keeps it brand-safe & calm
// ─────────────────────────────────────────────────────────────
function Mascot({ size = 72, mood = 'calm' }) {
  const accent = mood === 'alert' ? PT.highSolid : mood === 'watch' ? PT.modSolid : PT.primary;
  return (
    <svg width={size} height={size} viewBox="0 0 80 80" aria-hidden="true">
      <defs>
        <radialGradient id={`mg-${mood}`} cx="50%" cy="40%" r="60%">
          <stop offset="0%" stopColor="#fff" />
          <stop offset="100%" stopColor={mood === 'alert' ? '#FDE2E2' : mood === 'watch' ? '#FFF4CC' : '#DDEBFD'} />
        </radialGradient>
      </defs>
      <circle cx="40" cy="40" r="34" fill={`url(#mg-${mood})`} stroke={accent} strokeOpacity="0.25" strokeWidth="1.5" />
      <circle cx="30" cy="36" r="2.6" fill={PT.navy} />
      <circle cx="50" cy="36" r="2.6" fill={PT.navy} />
      <path d={mood === 'alert' ? 'M30 50q10 -5 20 0' : 'M30 48q10 6 20 0'}
        stroke={PT.navy} strokeWidth="2.4" fill="none" strokeLinecap="round" />
      <circle cx="22" cy="44" r="2.5" fill={accent} opacity="0.5" />
      <circle cx="58" cy="44" r="2.5" fill={accent} opacity="0.5" />
    </svg>
  );
}

// ─────────────────────────────────────────────────────────────
// Bottom tab bar
// ─────────────────────────────────────────────────────────────
function TabBar({ active, onChange }) {
  const tabs = [
    { id: 'home', label: 'Início', icon: Icon.home },
    { id: 'evaluate', label: 'Avaliar', icon: Icon.stetho },
    { id: 'orient', label: 'Orientações', icon: Icon.book },
    { id: 'history', label: 'Histórico', icon: Icon.history },
    { id: 'profile', label: 'Perfil', icon: Icon.user },
  ];
  return (
    <div style={{
      position: 'absolute', left: 12, right: 12, bottom: 14,
      background: 'rgba(255,255,255,0.92)', backdropFilter: 'blur(20px)',
      WebkitBackdropFilter: 'blur(20px)',
      borderRadius: 24, padding: '10px 6px',
      boxShadow: '0 16px 40px rgba(15,36,64,0.18), 0 0 0 1px rgba(15,36,64,0.05)',
      display: 'flex', justifyContent: 'space-around', zIndex: 30,
    }}>
      {tabs.map(t => {
        const I = t.icon;
        const isAct = active === t.id;
        const isMain = t.id === 'evaluate';
        if (isMain) {
          return (
            <button key={t.id} onClick={() => onChange(t.id)} style={{
              border: 'none', background: 'transparent', cursor: 'pointer',
              display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 2,
              transform: 'translateY(-22px)', padding: 0,
            }}>
              <div style={{
                width: 56, height: 56, borderRadius: 20,
                background: `linear-gradient(180deg, ${PT.secondary}, ${PT.primary})`,
                display: 'flex', alignItems: 'center', justifyContent: 'center',
                color: '#fff', boxShadow: `0 12px 24px ${PT.primary}55`,
              }}>
                <I size={26} sw={2.2} />
              </div>
              <div style={{ fontSize: 10, fontWeight: 800, color: PT.primaryDark, marginTop: 6 }}>{t.label}</div>
            </button>
          );
        }
        return (
          <button key={t.id} onClick={() => onChange(t.id)} style={{
            flex: 1, border: 'none', background: 'transparent', cursor: 'pointer',
            display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 4, padding: '6px 0',
            color: isAct ? PT.primary : PT.textSubtle,
          }}>
            <I size={22} sw={isAct ? 2.2 : 1.8} />
            <div style={{ fontSize: 10, fontWeight: isAct ? 800 : 600 }}>{t.label}</div>
          </button>
        );
      })}
    </div>
  );
}

Object.assign(window, {
  PT, Icon, Card, Pill, PrimaryButton, GhostButton,
  ScreenHeader, ProgressBar, Mascot, TabBar,
});
