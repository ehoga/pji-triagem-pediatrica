/* PediTriagem — screens (Home, Child, Symptoms, About, Orientations, History, Profile) */

const SYMPTOMS = [
  { id: 'fever', name: 'Febre', desc: 'Temperatura acima de 37.8°C', icon: Icon.thermo, color: '#F59E5C' },
  { id: 'cough', name: 'Tosse', desc: 'Seca, com catarro ou persistente', icon: Icon.cough, color: '#56CCF2' },
  { id: 'vomit', name: 'Vômitos', desc: 'Náuseas ou episódios de vômito', icon: Icon.vomit, color: '#9B7BE0' },
  { id: 'diarrhea', name: 'Diarreia', desc: 'Fezes líquidas ou frequentes', icon: Icon.drop, color: '#56CCF2' },
  { id: 'belly', name: 'Dor abdominal', desc: 'Dor ou desconforto na barriga', icon: Icon.belly, color: '#F2C94C' },
  { id: 'breath', name: 'Falta de ar', desc: 'Respiração rápida ou difícil', icon: Icon.lung, color: '#EB5757' },
  { id: 'rash', name: 'Manchas na pele', desc: 'Vermelhidão, pintas ou erupção', icon: Icon.rash, color: '#E18ABF' },
  { id: 'trauma', name: 'Trauma leve', desc: 'Quedas, batidas ou cortes pequenos', icon: Icon.bandage, color: '#7BC393' },
  { id: 'ear', name: 'Dor de ouvido', desc: 'Dor, coceira ou secreção', icon: Icon.ear, color: '#56CCF2' },
];

const CHILDREN = [
  { id: 'maria', name: 'Maria', age: '4 anos', weight: '17 kg', emoji: '🌸', tint: '#FCE7F3' },
  { id: 'lucas', name: 'Lucas', age: '8 meses', weight: '8.2 kg', emoji: '🌱', tint: '#DFF6EA' },
];

const HISTORY = [
  { id: 'h1', child: 'Maria', symptom: 'Febre', date: '12 mai 2026', risk: 'low', label: 'Baixo risco' },
  { id: 'h2', child: 'Maria', symptom: 'Tosse', date: '04 mai 2026', risk: 'mod', label: 'Risco moderado' },
  { id: 'h3', child: 'Lucas', symptom: 'Vômitos', date: '28 abr 2026', risk: 'low', label: 'Baixo risco' },
  { id: 'h4', child: 'Maria', symptom: 'Trauma leve', date: '15 abr 2026', risk: 'low', label: 'Baixo risco' },
  { id: 'h5', child: 'Lucas', symptom: 'Falta de ar', date: '02 abr 2026', risk: 'high', label: 'Alto risco' },
];

// ─────────────────────────────────────────────────────────────
// Home
// ─────────────────────────────────────────────────────────────
function HomeScreen({ go, child, setChild }) {
  return (
    <div style={{ paddingBottom: 120 }}>
      {/* Top bar */}
      <div style={{ padding: '8px 20px 4px', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <div>
          <div style={{ fontSize: 12, color: PT.textMuted, fontWeight: 600 }}>Olá, Camila</div>
          <div style={{ fontSize: 13, color: PT.text, fontWeight: 800, marginTop: 2 }}>Quarta, 4 mai</div>
        </div>
        <div style={{ display: 'flex', gap: 8 }}>
          <button style={{
            width: 38, height: 38, borderRadius: 12, border: 'none',
            background: '#fff', boxShadow: PT.shadow, cursor: 'pointer', color: PT.navy,
            display: 'flex', alignItems: 'center', justifyContent: 'center',
          }}><Icon.bell size={18} /></button>
        </div>
      </div>

      {/* Hero */}
      <div style={{ padding: '12px 20px 0' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: 14 }}>
          <Mascot size={64} />
          <div>
            <div style={{ fontSize: 22, fontWeight: 800, color: PT.text, lineHeight: 1.15 }}>
              Como está a<br /> criança hoje?
            </div>
            <div style={{ fontSize: 12, color: PT.textMuted, marginTop: 6, fontWeight: 600 }}>
              Vamos te ajudar com calma.
            </div>
          </div>
        </div>
      </div>

      {/* Child selector */}
      <div style={{ padding: '20px 20px 0' }}>
        <div style={{ fontSize: 11, fontWeight: 800, color: PT.textMuted, textTransform: 'uppercase', letterSpacing: 0.6, marginBottom: 8 }}>
          Avaliar para
        </div>
        <div style={{ display: 'flex', gap: 10, overflowX: 'auto', paddingBottom: 4 }}>
          {CHILDREN.map(c => {
            const sel = child?.id === c.id;
            return (
              <button key={c.id} onClick={() => setChild(c)} style={{
                border: 'none', cursor: 'pointer', padding: '12px 14px',
                borderRadius: 18, background: sel ? PT.navy : '#fff',
                color: sel ? '#fff' : PT.text,
                boxShadow: sel ? `0 10px 22px ${PT.navy}30` : PT.shadow,
                display: 'flex', alignItems: 'center', gap: 10, minWidth: 152,
                fontFamily: PT.font, textAlign: 'left',
              }}>
                <div style={{
                  width: 36, height: 36, borderRadius: 12, background: c.tint,
                  display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: 20,
                }}>{c.emoji}</div>
                <div>
                  <div style={{ fontSize: 14, fontWeight: 800 }}>{c.name}</div>
                  <div style={{ fontSize: 11, opacity: 0.75, fontWeight: 600 }}>{c.age}</div>
                </div>
              </button>
            );
          })}
          <button onClick={() => go('child-add')} style={{
            border: `1.5px dashed ${PT.hairline}`, background: 'transparent', cursor: 'pointer',
            borderRadius: 18, padding: '12px 14px', minWidth: 100,
            display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center',
            color: PT.primary, gap: 4, fontFamily: PT.font,
          }}>
            <Icon.plus size={20} />
            <div style={{ fontSize: 11, fontWeight: 800 }}>Adicionar</div>
          </button>
        </div>
      </div>

      {/* Big primary CTA */}
      <div style={{ padding: '18px 20px 0' }}>
        <div onClick={() => go('symptoms')} style={{
          background: `linear-gradient(135deg, ${PT.primary}, ${PT.navy} 130%)`,
          borderRadius: 24, padding: 20, color: '#fff', cursor: 'pointer',
          boxShadow: `0 18px 40px ${PT.primary}40`,
          display: 'flex', alignItems: 'center', gap: 14, position: 'relative', overflow: 'hidden',
        }}>
          <div style={{
            position: 'absolute', right: -30, bottom: -40, width: 160, height: 160,
            borderRadius: '50%', background: 'rgba(255,255,255,0.08)',
          }} />
          <div style={{
            width: 52, height: 52, borderRadius: 16,
            background: 'rgba(255,255,255,0.18)', display: 'flex',
            alignItems: 'center', justifyContent: 'center',
          }}>
            <Icon.stetho size={26} sw={2} />
          </div>
          <div style={{ flex: 1 }}>
            <div style={{ fontSize: 17, fontWeight: 800 }}>Avaliar sintomas</div>
            <div style={{ fontSize: 12, opacity: 0.85, marginTop: 2, fontWeight: 600 }}>
              Triagem guiada em ~2 minutos
            </div>
          </div>
          <Icon.chevR size={20} />
        </div>
      </div>

      {/* Quick actions */}
      <div style={{ padding: '16px 20px 0', display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 10 }}>
        <Card onClick={() => go('orient')} padding={14} style={{ display: 'flex', flexDirection: 'column', gap: 10 }}>
          <div style={{
            width: 36, height: 36, borderRadius: 12, background: PT.lowSofter,
            display: 'flex', alignItems: 'center', justifyContent: 'center', color: PT.lowSolid,
          }}><Icon.book size={20} /></div>
          <div>
            <div style={{ fontSize: 13, fontWeight: 800, color: PT.text }}>Orientações</div>
            <div style={{ fontSize: 11, color: PT.textMuted, marginTop: 2, fontWeight: 600 }}>
              Cuidados em casa
            </div>
          </div>
        </Card>
        <Card onClick={() => go('history')} padding={14} style={{ display: 'flex', flexDirection: 'column', gap: 10 }}>
          <div style={{
            width: 36, height: 36, borderRadius: 12, background: '#EEF4FE',
            display: 'flex', alignItems: 'center', justifyContent: 'center', color: PT.primary,
          }}><Icon.history size={20} /></div>
          <div>
            <div style={{ fontSize: 13, fontWeight: 800, color: PT.text }}>Histórico</div>
            <div style={{ fontSize: 11, color: PT.textMuted, marginTop: 2, fontWeight: 600 }}>
              5 avaliações
            </div>
          </div>
        </Card>
      </div>

      {/* Disclaimer */}
      <div style={{ padding: '16px 20px 0' }}>
        <Card padding={14} style={{
          background: '#EEF4FE', border: `1px solid ${PT.primary}22`,
          display: 'flex', gap: 12, alignItems: 'flex-start',
        }}>
          <div style={{ color: PT.primary, marginTop: 2 }}><Icon.shield size={20} /></div>
          <div>
            <div style={{ fontSize: 12, fontWeight: 800, color: PT.navy }}>
              Apoio à decisão, não diagnóstico
            </div>
            <div style={{ fontSize: 11, color: PT.textMuted, marginTop: 4, lineHeight: 1.4, fontWeight: 600 }}>
              Este aplicativo ajuda na orientação inicial, mas não substitui a avaliação de um pediatra.
            </div>
          </div>
        </Card>
      </div>

      {/* Recent */}
      <div style={{ padding: '20px 20px 0' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'baseline', marginBottom: 8 }}>
          <div style={{ fontSize: 14, fontWeight: 800, color: PT.text }}>Avaliações recentes</div>
          <button onClick={() => go('history')} style={{
            background: 'none', border: 'none', cursor: 'pointer', color: PT.primary,
            fontSize: 12, fontWeight: 800, fontFamily: PT.font,
          }}>Ver todas</button>
        </div>
        {HISTORY.slice(0, 2).map(h => <HistoryRow key={h.id} h={h} />)}
      </div>
    </div>
  );
}

function HistoryRow({ h, onClick }) {
  const tones = {
    low: { dot: PT.lowSolid, bg: PT.lowSofter, label: 'Baixo' },
    mod: { dot: PT.modSolid, bg: PT.modSofter, label: 'Moderado' },
    high: { dot: PT.highSolid, bg: PT.highSofter, label: 'Alto' },
  };
  const t = tones[h.risk];
  return (
    <Card padding={14} onClick={onClick} style={{
      marginBottom: 8, display: 'flex', alignItems: 'center', gap: 12,
    }}>
      <div style={{
        width: 42, height: 42, borderRadius: 12, background: t.bg,
        display: 'flex', alignItems: 'center', justifyContent: 'center',
      }}>
        <div style={{ width: 10, height: 10, borderRadius: '50%', background: t.dot }} />
      </div>
      <div style={{ flex: 1 }}>
        <div style={{ fontSize: 13, fontWeight: 800, color: PT.text }}>{h.child} · {h.symptom}</div>
        <div style={{ fontSize: 11, color: PT.textMuted, marginTop: 2, fontWeight: 600 }}>
          {h.date} · Risco {t.label.toLowerCase()}
        </div>
      </div>
      <Icon.chevR size={18} color={PT.textSubtle} />
    </Card>
  );
}

// ─────────────────────────────────────────────────────────────
// Add Child
// ─────────────────────────────────────────────────────────────
function ChildScreen({ go }) {
  const [name, setName] = React.useState('');
  const [age, setAge] = React.useState('4');
  const [unit, setUnit] = React.useState('anos');
  const [weight, setWeight] = React.useState('');
  const [avatar, setAvatar] = React.useState('🌸');
  const avatars = ['🌸', '🌱', '⭐', '🐻', '🦊', '🌈'];
  return (
    <div style={{ paddingBottom: 32 }}>
      <ScreenHeader title="Cadastrar criança" onBack={() => go('home')} />
      <div style={{ padding: '8px 20px 0' }}>
        <div style={{
          display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 8,
          marginBottom: 16,
        }}>
          <div style={{
            width: 80, height: 80, borderRadius: 28, background: '#FCE7F3',
            display: 'flex', alignItems: 'center', justifyContent: 'center',
            fontSize: 40, boxShadow: PT.shadow,
          }}>{avatar}</div>
          <div style={{ display: 'flex', gap: 6 }}>
            {avatars.map(a => (
              <button key={a} onClick={() => setAvatar(a)} style={{
                width: 32, height: 32, borderRadius: 10, border: 'none',
                background: avatar === a ? PT.navy : '#fff', cursor: 'pointer',
                boxShadow: PT.shadow, fontSize: 16,
              }}>{a}</button>
            ))}
          </div>
        </div>

        <Field label="Nome da criança">
          <input value={name} onChange={e => setName(e.target.value)} placeholder="Ex.: Maria"
            style={inputStyle} />
        </Field>

        <Field label="Idade">
          <div style={{ display: 'flex', gap: 8 }}>
            <input value={age} onChange={e => setAge(e.target.value)} type="number"
              style={{ ...inputStyle, flex: 1 }} />
            <div style={{ display: 'flex', background: '#fff', borderRadius: 14, padding: 4, boxShadow: PT.shadow }}>
              {['meses', 'anos'].map(u => (
                <button key={u} onClick={() => setUnit(u)} style={{
                  border: 'none', background: unit === u ? PT.primary : 'transparent',
                  color: unit === u ? '#fff' : PT.text, padding: '10px 14px',
                  borderRadius: 10, fontWeight: 800, fontSize: 13, cursor: 'pointer',
                  fontFamily: PT.font,
                }}>{u}</button>
              ))}
            </div>
          </div>
        </Field>

        <Field label="Peso (opcional)" hint="Ajuda em cálculos de doses e desidratação">
          <div style={{ display: 'flex', gap: 8, alignItems: 'center' }}>
            <input value={weight} onChange={e => setWeight(e.target.value)} placeholder="Ex.: 17"
              style={{ ...inputStyle, flex: 1 }} type="number" />
            <div style={{
              padding: '12px 14px', background: '#fff', borderRadius: 14,
              fontWeight: 800, color: PT.textMuted, boxShadow: PT.shadow, fontSize: 14,
            }}>kg</div>
          </div>
        </Field>

        <div style={{ marginTop: 24 }}>
          <PrimaryButton onClick={() => go('symptoms')} icon={<Icon.check size={18} />}>
            Salvar e continuar
          </PrimaryButton>
        </div>
      </div>
    </div>
  );
}

const inputStyle = {
  width: '100%', padding: '14px 16px', borderRadius: 14, border: 'none',
  background: '#fff', boxShadow: PT.shadow, fontSize: 15, fontWeight: 600,
  color: PT.text, fontFamily: PT.font, outline: 'none', boxSizing: 'border-box',
};

function Field({ label, hint, children }) {
  return (
    <div style={{ marginBottom: 14 }}>
      <div style={{ fontSize: 12, fontWeight: 800, color: PT.text, marginBottom: 6 }}>{label}</div>
      {children}
      {hint && <div style={{ fontSize: 11, color: PT.textMuted, marginTop: 6, fontWeight: 600 }}>{hint}</div>}
    </div>
  );
}

// ─────────────────────────────────────────────────────────────
// Symptoms
// ─────────────────────────────────────────────────────────────
function SymptomsScreen({ go, child, pickSymptom }) {
  const [q, setQ] = React.useState('');
  const filtered = SYMPTOMS.filter(s => s.name.toLowerCase().includes(q.toLowerCase()));
  return (
    <div style={{ paddingBottom: 32 }}>
      <ScreenHeader title="Selecionar sintoma"
        subtitle={child ? `Para ${child.name}, ${child.age}` : null}
        onBack={() => go('home')} />
      <div style={{ padding: '0 20px' }}>
        <div style={{
          display: 'flex', alignItems: 'center', gap: 10, padding: '12px 14px',
          background: '#fff', borderRadius: 14, boxShadow: PT.shadow, marginBottom: 16,
        }}>
          <Icon.search size={18} color={PT.textSubtle} />
          <input value={q} onChange={e => setQ(e.target.value)}
            placeholder="Buscar sintoma..."
            style={{
              flex: 1, border: 'none', outline: 'none', fontSize: 14, fontFamily: PT.font,
              fontWeight: 600, color: PT.text, background: 'transparent',
            }} />
        </div>

        <div style={{ fontSize: 11, fontWeight: 800, color: PT.textMuted, textTransform: 'uppercase', letterSpacing: 0.6, marginBottom: 10 }}>
          Qual o sintoma principal?
        </div>

        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 10 }}>
          {filtered.map(s => {
            const I = s.icon;
            return (
              <Card key={s.id} padding={14} onClick={() => { pickSymptom(s); go('quiz'); }}
                style={{ display: 'flex', flexDirection: 'column', gap: 10, minHeight: 116 }}>
                <div style={{
                  width: 38, height: 38, borderRadius: 12,
                  background: `${s.color}22`, color: s.color,
                  display: 'flex', alignItems: 'center', justifyContent: 'center',
                }}><I size={22} sw={2} /></div>
                <div>
                  <div style={{ fontSize: 13, fontWeight: 800, color: PT.text }}>{s.name}</div>
                  <div style={{ fontSize: 10.5, color: PT.textMuted, marginTop: 3, fontWeight: 600, lineHeight: 1.35 }}>
                    {s.desc}
                  </div>
                </div>
              </Card>
            );
          })}
        </div>

        <div style={{ marginTop: 16 }}>
          <Card padding={14} style={{ display: 'flex', gap: 12, alignItems: 'center', background: PT.highSofter, border: `1px solid ${PT.highSolid}33` }}>
            <div style={{ color: PT.highSolid }}><Icon.phone size={20} /></div>
            <div style={{ flex: 1 }}>
              <div style={{ fontSize: 12, fontWeight: 800, color: '#A33333' }}>Emergência?</div>
              <div style={{ fontSize: 10.5, color: '#7A2929', marginTop: 2, fontWeight: 600 }}>
                Em risco imediato, ligue 192 (SAMU).
              </div>
            </div>
            <button style={{
              padding: '8px 14px', borderRadius: 10, border: 'none',
              background: PT.highSolid, color: '#fff', fontWeight: 800, cursor: 'pointer',
              fontSize: 12, fontFamily: PT.font,
            }}>Ligar</button>
          </Card>
        </div>
      </div>
    </div>
  );
}

Object.assign(window, {
  SYMPTOMS, CHILDREN, HISTORY,
  HomeScreen, HistoryRow, ChildScreen, SymptomsScreen,
  Field, inputStyle,
});
